package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class VoiceAndTtsManager(private val context: Context) {

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var pendingSpeech: String? = null

    private var speechRecognizer: SpeechRecognizer? = null

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isVoiceMuted = MutableStateFlow(false)
    val isVoiceMuted: StateFlow<Boolean> = _isVoiceMuted.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _listeningLabel = MutableStateFlow("")
    val listeningLabel: StateFlow<String> = _listeningLabel.asStateFlow()

    init {
        initTts()
    }

    private fun initTts() {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val ptBr = Locale("pt", "BR")
                val res = tts?.setLanguage(ptBr)
                ttsReady = res != TextToSpeech.LANG_MISSING_DATA && res != TextToSpeech.LANG_NOT_SUPPORTED
                if (!ttsReady) {
                    tts?.language = Locale.getDefault()
                    ttsReady = true
                }
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }
                })
                pendingSpeech?.let { text ->
                    pendingSpeech = null
                    speak(text)
                }
            } else {
                Log.w(TAG, "TextToSpeech init failed with status $status")
            }
        }
    }

    fun toggleMute(): Boolean {
        val next = !_isVoiceMuted.value
        _isVoiceMuted.value = next
        if (next) {
            stopSpeaking()
        }
        return next
    }

    fun setMuted(muted: Boolean) {
        _isVoiceMuted.value = muted
        if (muted) stopSpeaking()
    }

    fun speak(text: String) {
        if (_isVoiceMuted.value) return
        val clean = text
            .replace("*", "")
            .replace("#", "")
            .replace("`", "")
            .trim()
        if (clean.isEmpty()) return

        if (!ttsReady) {
            pendingSpeech = clean
            return
        }
        tts?.speak(clean, TextToSpeech.QUEUE_FLUSH, null, "nexus_utterance_${System.currentTimeMillis()}")
    }

    fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun isInlineSpeechAvailable(): Boolean {
        return runCatching { SpeechRecognizer.isRecognitionAvailable(context) }.getOrDefault(false)
    }

    fun createSpeechIntent(prompt: String = "Fale agora em português..."): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "pt-BR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
        }
    }

    fun startInlineListening(
        label: String,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        stopListening()
        if (!isInlineSpeechAvailable()) {
            onError("Reconhecimento de voz nativo indisponível neste aparelho.")
            return
        }

        _listeningLabel.value = label
        _isListening.value = true

        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer = recognizer

        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _isListening.value = true
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                _isListening.value = false
            }

            override fun onError(error: Int) {
                _isListening.value = false
                val msg = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Erro ao capturar áudio do microfone."
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permissão de microfone necessária."
                    SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                        "Sem conexão para reconhecimento de voz."
                    SpeechRecognizer.ERROR_NO_MATCH -> "Não entendi sua fala. Tente falar novamente."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Nenhuma fala detectada."
                    else -> "Não foi possível reconhecer a voz (código $error)."
                }
                onError(msg)
            }

            override fun onResults(results: Bundle?) {
                _isListening.value = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val best = matches?.firstOrNull()?.trim().orEmpty()
                if (best.isNotEmpty()) {
                    onResult(best)
                } else {
                    onError("Não foi possível transcrever a fala.")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        runCatching {
            recognizer.startListening(createSpeechIntent(label))
        }.onFailure { err ->
            _isListening.value = false
            onError(err.localizedMessage ?: "Falha ao iniciar o microfone.")
        }
    }

    fun stopListening() {
        _isListening.value = false
        runCatching {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
        }
        speechRecognizer = null
    }

    fun release() {
        stopListening()
        stopSpeaking()
        tts?.shutdown()
        tts = null
    }

    companion object {
        private const val TAG = "VoiceAndTtsManager"
    }
}
