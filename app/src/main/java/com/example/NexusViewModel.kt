package com.example

import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AiRepository
import com.example.data.AiTool
import com.example.data.CommercialChannel
import com.example.data.CommercialCopyResult
import com.example.data.GeneratedWebsite
import com.example.data.LocalPreferencesStore
import com.example.data.PollinationsAiRepository
import com.example.data.SavedSiteEntry
import com.example.data.ToolAssistantResponse
import com.example.data.ToolCategory
import com.example.data.ToolsCatalog
import com.example.data.UrlAnalysisResult
import com.example.data.WeatherInfo
import com.example.data.WeatherRepository
import com.example.data.WebsiteFormInput
import com.example.util.MathEvaluator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import java.util.UUID

sealed interface WebsiteUiState {
    data object Idle : WebsiteUiState
    data object Loading : WebsiteUiState
    data class Success(val website: GeneratedWebsite) : WebsiteUiState
    data class Error(val message: String) : WebsiteUiState
}

sealed interface AntivirusUiState {
    data object Idle : AntivirusUiState
    data object Loading : AntivirusUiState
    data class Success(val result: UrlAnalysisResult) : AntivirusUiState
    data class Error(val message: String) : AntivirusUiState
}

sealed interface AssistantUiState {
    data object Idle : AssistantUiState
    data object Loading : AssistantUiState
    data class Success(val response: ToolAssistantResponse) : AssistantUiState
    data class Error(val message: String) : AssistantUiState
}

sealed interface CommercialCopyUiState {
    data object Idle : CommercialCopyUiState
    data object Loading : CommercialCopyUiState
    data class Success(val copy: CommercialCopyResult) : CommercialCopyUiState
    data class Error(val message: String) : CommercialCopyUiState
}

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(val weather: WeatherInfo) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

sealed interface UniversalAssistantUiState {
    data object Idle : UniversalAssistantUiState
    data object Loading : UniversalAssistantUiState
    data class Answer(val title: String, val body: String) : UniversalAssistantUiState
    data class Error(val message: String) : UniversalAssistantUiState
}

class NexusViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsStore = LocalPreferencesStore(application.applicationContext)
    private val aiRepository: AiRepository = PollinationsAiRepository()
    private val weatherRepository = WeatherRepository()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Free Quota / VIP Unlock
    private val _isVipUnlocked = MutableStateFlow(prefsStore.isVipUnlocked())
    val isVipUnlocked: StateFlow<Boolean> = _isVipUnlocked.asStateFlow()

    private val _remainingFreeUsesToday = MutableStateFlow(prefsStore.getRemainingFreeUsesToday())
    val remainingFreeUsesToday: StateFlow<Int> = _remainingFreeUsesToday.asStateFlow()

    private val _isVipDialogOpen = MutableStateFlow(false)
    val isVipDialogOpen: StateFlow<Boolean> = _isVipDialogOpen.asStateFlow()

    private val _vipActivationError = MutableStateFlow<String?>(null)
    val vipActivationError: StateFlow<String?> = _vipActivationError.asStateFlow()

    // Live Weather (Open-Meteo)
    private val _weatherCityInput = MutableStateFlow("Floriano - PI")
    val weatherCityInput: StateFlow<String> = _weatherCityInput.asStateFlow()

    private val _weatherUiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherUiState: StateFlow<WeatherUiState> = _weatherUiState.asStateFlow()

    // Native Utilities Modals
    private val _isAlarmSheetOpen = MutableStateFlow(false)
    val isAlarmSheetOpen: StateFlow<Boolean> = _isAlarmSheetOpen.asStateFlow()

    private val _alarmInitialHour = MutableStateFlow<Int?>(null)
    val alarmInitialHour: StateFlow<Int?> = _alarmInitialHour.asStateFlow()

    private val _alarmInitialMinute = MutableStateFlow<Int?>(null)
    val alarmInitialMinute: StateFlow<Int?> = _alarmInitialMinute.asStateFlow()

    private val _alarmInitialLabel = MutableStateFlow("Lembrete NEXUS AI 100")
    val alarmInitialLabel: StateFlow<String> = _alarmInitialLabel.asStateFlow()

    private val _activeInAppAlarmLabel = MutableStateFlow<String?>(null)
    val activeInAppAlarmLabel: StateFlow<String?> = _activeInAppAlarmLabel.asStateFlow()

    private val _activeInAppAlarmTimeText = MutableStateFlow<String?>(null)
    val activeInAppAlarmTimeText: StateFlow<String?> = _activeInAppAlarmTimeText.asStateFlow()

    private val _ringingAlarmLabel = MutableStateFlow<String?>(null)
    val ringingAlarmLabel: StateFlow<String?> = _ringingAlarmLabel.asStateFlow()

    private var alarmCountdownJob: Job? = null
    private var activeRingtone: Ringtone? = null

    private val _isCalculatorOpen = MutableStateFlow(false)
    val isCalculatorOpen: StateFlow<Boolean> = _isCalculatorOpen.asStateFlow()

    private val _calcInitialExpression = MutableStateFlow("")
    val calcInitialExpression: StateFlow<String> = _calcInitialExpression.asStateFlow()

    private val _calcInitialResult = MutableStateFlow("0")
    val calcInitialResult: StateFlow<String> = _calcInitialResult.asStateFlow()

    private val _youtubeQueryOpen = MutableStateFlow<String?>(null)
    val youtubeQueryOpen: StateFlow<String?> = _youtubeQueryOpen.asStateFlow()

    private val _isNexusLandingOpen = MutableStateFlow(false)
    val isNexusLandingOpen: StateFlow<Boolean> = _isNexusLandingOpen.asStateFlow()

    // Universal Voice Assistant
    private val _isVoiceSheetOpen = MutableStateFlow(false)
    val isVoiceSheetOpen: StateFlow<Boolean> = _isVoiceSheetOpen.asStateFlow()

    private val _recognizedVoiceText = MutableStateFlow("")
    val recognizedVoiceText: StateFlow<String> = _recognizedVoiceText.asStateFlow()

    private val _universalAssistantUiState = MutableStateFlow<UniversalAssistantUiState>(UniversalAssistantUiState.Idle)
    val universalAssistantUiState: StateFlow<UniversalAssistantUiState> = _universalAssistantUiState.asStateFlow()

    // Main screen spotlight: Corporate AI Antivirus (URL, Pix Copia e Cola, Boleto)
    private val _corporateUrlInput = MutableStateFlow("")
    val corporateUrlInput: StateFlow<String> = _corporateUrlInput.asStateFlow()

    private val _corporateAntivirusUiState = MutableStateFlow<AntivirusUiState>(AntivirusUiState.Idle)
    val corporateAntivirusUiState: StateFlow<AntivirusUiState> = _corporateAntivirusUiState.asStateFlow()

    // Tab 1: Website Creator & Game Creator + Last 5 Sites History
    private val _isGameMode = MutableStateFlow(false)
    val isGameMode: StateFlow<Boolean> = _isGameMode.asStateFlow()

    private val _gameIdeaInput = MutableStateFlow("")
    val gameIdeaInput: StateFlow<String> = _gameIdeaInput.asStateFlow()

    private val _websiteForm = MutableStateFlow(WebsiteFormInput())
    val websiteForm: StateFlow<WebsiteFormInput> = _websiteForm.asStateFlow()

    private val _websiteUiState = MutableStateFlow<WebsiteUiState>(WebsiteUiState.Idle)
    val websiteUiState: StateFlow<WebsiteUiState> = _websiteUiState.asStateFlow()

    private val _siteHistory = MutableStateFlow(prefsStore.getSiteHistory())
    val siteHistory: StateFlow<List<SavedSiteEntry>> = _siteHistory.asStateFlow()

    // Tab 2: AI Antivirus
    private val _urlInput = MutableStateFlow("")
    val urlInput: StateFlow<String> = _urlInput.asStateFlow()

    private val _antivirusUiState = MutableStateFlow<AntivirusUiState>(AntivirusUiState.Idle)
    val antivirusUiState: StateFlow<AntivirusUiState> = _antivirusUiState.asStateFlow()

    // Tab 3: Commercial Copy Generator + 100 AI Tools
    private val _commercialChannel = MutableStateFlow(CommercialChannel.WHATSAPP)
    val commercialChannel: StateFlow<CommercialChannel> = _commercialChannel.asStateFlow()

    private val _commercialBusiness = MutableStateFlow("")
    val commercialBusiness: StateFlow<String> = _commercialBusiness.asStateFlow()

    private val _commercialTopic = MutableStateFlow("")
    val commercialTopic: StateFlow<String> = _commercialTopic.asStateFlow()

    private val _commercialCopyUiState = MutableStateFlow<CommercialCopyUiState>(CommercialCopyUiState.Idle)
    val commercialCopyUiState: StateFlow<CommercialCopyUiState> = _commercialCopyUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ToolCategory.TODAS)
    val selectedCategory: StateFlow<ToolCategory> = _selectedCategory.asStateFlow()

    private val _favoritesOnly = MutableStateFlow(false)
    val favoritesOnly: StateFlow<Boolean> = _favoritesOnly.asStateFlow()

    private val _favoriteIds = MutableStateFlow(prefsStore.getFavoriteToolIds())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    val filteredTools: StateFlow<List<AiTool>> = combine(
        _searchQuery,
        _selectedCategory,
        _favoritesOnly,
        _favoriteIds
    ) { query, category, onlyFavs, favIds ->
        val normalizedQuery = query.trim().lowercase()
        ToolsCatalog.allTools.filter { tool ->
            val matchesCategory = category == ToolCategory.TODAS || tool.category == category
            val matchesFavorite = !onlyFavs || favIds.contains(tool.id)
            val matchesQuery = normalizedQuery.isEmpty() ||
                tool.name.lowercase().contains(normalizedQuery) ||
                tool.description.lowercase().contains(normalizedQuery) ||
                tool.tag.lowercase().contains(normalizedQuery) ||
                tool.category.label.lowercase().contains(normalizedQuery)
            matchesCategory && matchesFavorite && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ToolsCatalog.allTools)

    private val _activeAssistantTool = MutableStateFlow<AiTool?>(null)
    val activeAssistantTool: StateFlow<AiTool?> = _activeAssistantTool.asStateFlow()

    private val _assistantQuestion = MutableStateFlow("")
    val assistantQuestion: StateFlow<String> = _assistantQuestion.asStateFlow()

    private val _assistantUiState = MutableStateFlow<AssistantUiState>(AssistantUiState.Idle)
    val assistantUiState: StateFlow<AssistantUiState> = _assistantUiState.asStateFlow()

    init {
        refreshWeatherByCity()
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index.coerceIn(0, 2)
    }

    // VIP Unlock & Daily Free Quota Controls
    fun openVipDialog() {
        _remainingFreeUsesToday.value = prefsStore.getRemainingFreeUsesToday()
        _vipActivationError.value = null
        _isVipDialogOpen.value = true
    }

    fun closeVipDialog() {
        _vipActivationError.value = null
        _isVipDialogOpen.value = false
    }

    fun activateVipWithCode(code: String): Boolean {
        val success = prefsStore.activateVipWithCode(code)
        if (success) {
            _isVipUnlocked.value = true
            _remainingFreeUsesToday.value = LocalPreferencesStore.MAX_FREE_DAILY_USES
            _vipActivationError.value = null
            _isVipDialogOpen.value = false
            return true
        } else {
            _vipActivationError.value = "Código VIP inválido. Confira o código recebido ou desbloqueie pelo Mercado Pago."
            return false
        }
    }

    private fun ensureAiQuotaOrShowVip(): Boolean {
        val remaining = prefsStore.getRemainingFreeUsesToday()
        _remainingFreeUsesToday.value = remaining
        if (_isVipUnlocked.value || prefsStore.hasFreeUsesRemainingToday()) {
            return true
        }
        _vipActivationError.value = null
        _isVipDialogOpen.value = true
        return false
    }

    private fun consumeAiCreditOnSuccess() {
        if (!_isVipUnlocked.value) {
            _remainingFreeUsesToday.value = prefsStore.consumeFreeUseToday()
        }
    }

    // Weather (Open-Meteo)
    fun updateWeatherCityInput(city: String) {
        _weatherCityInput.value = city
    }

    fun refreshWeatherByCity() {
        val city = _weatherCityInput.value.trim().ifEmpty { "Floriano - PI" }
        _weatherCityInput.value = city
        viewModelScope.launch {
            _weatherUiState.value = WeatherUiState.Loading
            try {
                val info = weatherRepository.fetchWeatherByCity(city)
                _weatherUiState.value = WeatherUiState.Success(info)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _weatherUiState.value = WeatherUiState.Error(
                    e.localizedMessage ?: "Não foi possível carregar o clima."
                )
            }
        }
    }

    fun fetchWeatherByGps(latitude: Double, longitude: Double, resolvedLabel: String?) {
        viewModelScope.launch {
            _weatherUiState.value = WeatherUiState.Loading
            try {
                val info = weatherRepository.fetchWeatherByCoordinates(latitude, longitude, resolvedLabel)
                if (!resolvedLabel.isNullOrBlank()) {
                    _weatherCityInput.value = resolvedLabel
                }
                _weatherUiState.value = WeatherUiState.Success(info)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _weatherUiState.value = WeatherUiState.Error(
                    e.localizedMessage ?: "Erro ao obter previsão por GPS."
                )
            }
        }
    }

    // Alarm Controls
    fun openAlarmSheet(hour: Int? = null, minute: Int? = null, label: String = "Lembrete NEXUS AI 100") {
        _alarmInitialHour.value = hour
        _alarmInitialMinute.value = minute
        _alarmInitialLabel.value = label
        _isAlarmSheetOpen.value = true
    }

    fun closeAlarmSheet() {
        _isAlarmSheetOpen.value = false
    }

    fun scheduleInAppAlarm(hour: Int, minute: Int, label: String) {
        alarmCountdownJob?.cancel()
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= now.timeInMillis) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        val delayMs = (target.timeInMillis - now.timeInMillis).coerceAtLeast(1000L)
        val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        _activeInAppAlarmLabel.value = label
        _activeInAppAlarmTimeText.value = formattedTime
        _isAlarmSheetOpen.value = false

        alarmCountdownJob = viewModelScope.launch {
            delay(delayMs)
            _activeInAppAlarmLabel.value = null
            _activeInAppAlarmTimeText.value = null
            triggerAlarmSoundAndVibration(label)
        }
    }

    fun cancelInAppAlarm() {
        alarmCountdownJob?.cancel()
        alarmCountdownJob = null
        _activeInAppAlarmLabel.value = null
        _activeInAppAlarmTimeText.value = null
    }

    fun triggerAlarmSoundAndVibration(label: String = "Alarme NEXUS AI 100") {
        _ringingAlarmLabel.value = label
        val ctx = getApplication<Application>().applicationContext
        runCatching {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(ctx, alarmUri)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ringtone?.isLooping = true
            }
            ringtone?.audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            ringtone?.play()
            activeRingtone = ringtone
        }
        runCatching {
            val pattern = longArrayOf(0, 500, 300, 500, 300)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(pattern, 0)
                }
            }
        }
    }

    fun stopAlarmSoundAndVibration() {
        _ringingAlarmLabel.value = null
        runCatching {
            activeRingtone?.stop()
            activeRingtone = null
        }
        val ctx = getApplication<Application>().applicationContext
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator.cancel()
            } else {
                @Suppress("DEPRECATION")
                val vibrator = ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.cancel()
            }
        }
    }

    // Calculator Controls
    fun openCalculator(initialExpr: String = "", initialRes: String = "0") {
        _calcInitialExpression.value = initialExpr
        _calcInitialResult.value = initialRes
        _isCalculatorOpen.value = true
    }

    fun closeCalculator() {
        _isCalculatorOpen.value = false
    }

    // YouTube Search Controls
    fun openYouTubeSearch(query: String = "") {
        _youtubeQueryOpen.value = query
    }

    fun closeYouTubeSearch() {
        _youtubeQueryOpen.value = null
    }

    // Nexus IA Landing Page Showcase Controls
    fun openNexusLanding() {
        _isNexusLandingOpen.value = true
    }

    fun closeNexusLanding() {
        _isNexusLandingOpen.value = false
    }

    // Universal Voice Assistant Controls & Command Routing
    fun openVoiceAssistantSheet() {
        _isVoiceSheetOpen.value = true
    }

    fun closeVoiceAssistantSheet() {
        _isVoiceSheetOpen.value = false
    }

    fun routeVoiceCommand(
        spokenText: String,
        onSpeakResponse: (String) -> Unit,
        onOpenMapsGps: (String) -> Unit
    ) {
        val clean = spokenText.trim()
        if (clean.isEmpty()) return
        _recognizedVoiceText.value = clean
        val lower = clean.lowercase(Locale("pt", "BR"))

        // 1. Criar Jogo IA
        if (lower.contains("criar jogo") || lower.contains("fazer jogo") || lower.contains("gerar jogo")) {
            val idea = clean
                .replace(Regex("(?i)criar jogo( de| sobre| para)?"), "")
                .replace(Regex("(?i)fazer jogo( de| sobre| para)?"), "")
                .replace(Regex("(?i)gerar jogo( de| sobre| para)?"), "")
                .trim()
                .ifEmpty { clean }
            _isVoiceSheetOpen.value = false
            _selectedTab.value = 0
            _isGameMode.value = true
            _gameIdeaInput.value = idea
            if (!ensureAiQuotaOrShowVip()) {
                onSpeakResponse("Limite diário gratuito atingido. Desbloqueie o Modo VIP para criar jogos sem limites.")
                return
            }
            onSpeakResponse("Abrindo o Criador de Jogos IA para $idea.")
            generateGame()
            return
        }

        // 2. Criar Site IA
        if (lower.contains("criar site") || lower.contains("fazer site") || lower.contains("gerar site")) {
            val idea = clean
                .replace(Regex("(?i)criar site( de| sobre| para)?"), "")
                .replace(Regex("(?i)fazer site( de| sobre| para)?"), "")
                .replace(Regex("(?i)gerar site( de| sobre| para)?"), "")
                .trim()
                .ifEmpty { clean }
            _isVoiceSheetOpen.value = false
            _selectedTab.value = 0
            _isGameMode.value = false
            _websiteForm.value = _websiteForm.value.copy(
                companyName = idea.replaceFirstChar { it.uppercase() }.take(38),
                niche = idea,
                services = "Criado via comando de voz: $idea"
            )
            if (!ensureAiQuotaOrShowVip()) {
                onSpeakResponse("Limite diário gratuito atingido. Desbloqueie o Modo VIP para criar sites sem limites.")
                return
            }
            onSpeakResponse("Abrindo o Criador de Sites IA para $idea.")
            generateWebsite()
            return
        }

        // 3. YouTube / Tocar
        if (lower.contains("youtube") || lower.startsWith("tocar ") || lower.contains("tocar ")) {
            val query = clean
                .replace(Regex("(?i)(abrir|buscar|pesquisar|tocar|ver|no|na)?\\s*youtube"), "")
                .replace(Regex("(?i)^tocar\\s+"), "")
                .trim()
                .ifEmpty { clean }
            _isVoiceSheetOpen.value = false
            onSpeakResponse("Abrindo busca no YouTube para $query.")
            openYouTubeSearch(query)
            return
        }

        // 4. GPS / Mapa / Como chegar
        if (lower.contains("gps") || lower.contains("mapa") || lower.contains("como chegar")) {
            val destination = clean
                .replace(Regex("(?i)(abrir|usar|no|na)?\\s*(gps|mapa)"), "")
                .replace(Regex("(?i)como chegar( em| no| na| para| até)?"), "")
                .trim()
                .ifEmpty { _weatherCityInput.value }
            _isVoiceSheetOpen.value = false
            onSpeakResponse("Abrindo rota no GPS para $destination.")
            onOpenMapsGps(destination)
            return
        }

        // 5. Alarme / Despertador
        if (lower.contains("alarme") || lower.contains("despertador")) {
            val (parsedHour, parsedMin) = extractSpokenTime(lower)
            _isVoiceSheetOpen.value = false
            openAlarmSheet(
                hour = parsedHour,
                minute = parsedMin,
                label = "Alarme por voz"
            )
            val timeSpoken = if (parsedHour != null && parsedMin != null) {
                "para ${parsedHour} horas e ${parsedMin} minutos"
            } else {
                ""
            }
            onSpeakResponse("Abrindo o despertador $timeSpoken.")
            return
        }

        // 6. Conta matemática falada ou abrir calculadora
        val mathPair = MathEvaluator.extractAndEvaluateSpokenMath(clean)
        if (mathPair != null) {
            val (expr, value) = mathPair
            val formatted = MathEvaluator.formatResult(value)
            val spoken = MathEvaluator.formatSpokenPortuguese(value)
            _isVoiceSheetOpen.value = false
            openCalculator(initialExpr = expr.replace("*", "×").replace("/", "÷"), initialRes = formatted)
            onSpeakResponse("O resultado é $spoken")
            return
        }
        if (lower.contains("calculadora")) {
            _isVoiceSheetOpen.value = false
            openCalculator()
            onSpeakResponse("Abrindo a calculadora falante.")
            return
        }

        if (lower.contains("landing page") || lower.contains("página de vendas") || lower.contains("nexus ia")) {
            _isVoiceSheetOpen.value = false
            openNexusLanding()
            onSpeakResponse("Abrindo a Landing Page de alta conversão Nexus IA.")
            return
        }

        // 7. Perguntas e Pesquisas Gerais com IA (Pollinations AI + TTS pt-BR)
        if (!ensureAiQuotaOrShowVip()) {
            _isVoiceSheetOpen.value = false
            onSpeakResponse("Limite diário gratuito atingido. Desbloqueie o Modo VIP para continuar usando sem limites.")
            return
        }
        _isVoiceSheetOpen.value = true
        viewModelScope.launch {
            _universalAssistantUiState.value = UniversalAssistantUiState.Loading
            try {
                val answer = aiRepository.askUniversalQuestion(clean)
                consumeAiCreditOnSuccess()
                _universalAssistantUiState.value = UniversalAssistantUiState.Answer(
                    title = "Resposta NEXUS AI",
                    body = answer
                )
                onSpeakResponse(answer)
            } catch (e: CancellationException) {
                throw e
            } catch (e: TimeoutCancellationException) {
                _universalAssistantUiState.value = UniversalAssistantUiState.Error(
                    "Tempo limite excedido ao consultar a IA. Tente novamente."
                )
            } catch (e: Exception) {
                _universalAssistantUiState.value = UniversalAssistantUiState.Error(
                    e.localizedMessage ?: "Erro ao responder sua pergunta."
                )
            }
        }
    }

    private fun extractSpokenTime(text: String): Pair<Int?, Int?> {
        val colonMatch = Regex("""(\d{1,2})[:h](\d{1,2})""").find(text)
        if (colonMatch != null) {
            val h = colonMatch.groupValues[1].toIntOrNull()?.coerceIn(0, 23)
            val m = colonMatch.groupValues[2].toIntOrNull()?.coerceIn(0, 59)
            return h to m
        }
        val wordsMatch = Regex("""(\d{1,2})\s*(horas?|h)?\s*(e\s*(\d{1,2}))?""").find(text)
        if (wordsMatch != null) {
            val h = wordsMatch.groupValues[1].toIntOrNull()?.coerceIn(0, 23)
            val m = wordsMatch.groupValues[4].toIntOrNull()?.coerceIn(0, 59) ?: 0
            return h to m
        }
        return null to null
    }

    // Corporate Antivirus on Main Screen (URL, Pix Copia e Cola, Boleto)
    fun updateCorporateUrlInput(url: String) {
        _corporateUrlInput.value = url
    }

    fun verifyCorporateLink() {
        val target = _corporateUrlInput.value.trim()
        if (target.length < 6) {
            _corporateAntivirusUiState.value = AntivirusUiState.Error(
                "Cole uma URL suspeita, código Pix Copia e Cola ou linha digitável de boleto para verificar."
            )
            return
        }
        if (!ensureAiQuotaOrShowVip()) return
        viewModelScope.launch {
            _corporateAntivirusUiState.value = AntivirusUiState.Loading
            try {
                val analysis = aiRepository.analyzeUrl(target, corporateMode = true)
                consumeAiCreditOnSuccess()
                _corporateAntivirusUiState.value = AntivirusUiState.Success(analysis)
            } catch (e: CancellationException) {
                throw e
            } catch (e: TimeoutCancellationException) {
                _corporateAntivirusUiState.value = AntivirusUiState.Error(
                    "Tempo limite excedido ao analisar com IA. Tente novamente."
                )
            } catch (e: Exception) {
                _corporateAntivirusUiState.value = AntivirusUiState.Error(
                    e.localizedMessage ?: "Erro ao verificar conteúdo com a IA."
                )
            }
        }
    }

    // Site / Game Creator
    fun setGameMode(enabled: Boolean) {
        _isGameMode.value = enabled
    }

    fun updateGameIdeaInput(idea: String) {
        _gameIdeaInput.value = idea
    }

    fun updateWebsiteForm(transform: (WebsiteFormInput) -> WebsiteFormInput) {
        _websiteForm.value = transform(_websiteForm.value)
    }

    fun fillSampleWebsiteForm() {
        _isGameMode.value = false
        _websiteForm.value = WebsiteFormInput(
            companyName = "Apex Energia Solar",
            niche = "Energia Solar Residencial e Comercial",
            whatsapp = "(11) 98765-4321",
            cityState = "São Paulo - SP",
            services = "Projeto completo e homologação, Instalação em até 48h, Garantia de 25 anos, Monitoramento pelo celular"
        )
    }

    fun generateWebsite() {
        val current = _websiteForm.value
        if (current.companyName.isBlank() || current.niche.isBlank()) {
            _websiteUiState.value = WebsiteUiState.Error(
                "Preencha pelo menos o Nome da Empresa e o Ramo/Especialidade para gerar o site."
            )
            return
        }
        if (!ensureAiQuotaOrShowVip()) return
        viewModelScope.launch {
            _websiteUiState.value = WebsiteUiState.Loading
            try {
                val result = aiRepository.generateWebsite(current)
                consumeAiCreditOnSuccess()
                _websiteUiState.value = WebsiteUiState.Success(result)

                val entry = SavedSiteEntry(
                    id = UUID.randomUUID().toString(),
                    companyName = result.companyName,
                    niche = current.niche.trim(),
                    whatsapp = current.whatsapp.trim(),
                    cityState = current.cityState.trim(),
                    services = current.services.trim(),
                    htmlCode = result.htmlCode,
                    createdAtMillis = System.currentTimeMillis()
                )
                _siteHistory.value = prefsStore.saveSiteToHistory(entry)
            } catch (e: CancellationException) {
                throw e
            } catch (e: TimeoutCancellationException) {
                _websiteUiState.value = WebsiteUiState.Error(
                    "Tempo limite excedido ao gerar o site com IA. Tente novamente."
                )
            } catch (e: Exception) {
                _websiteUiState.value = WebsiteUiState.Error(
                    e.localizedMessage ?: "Não foi possível gerar o site no momento."
                )
            }
        }
    }

    fun generateGame() {
        val idea = _gameIdeaInput.value.trim()
        if (idea.isEmpty()) {
            _websiteUiState.value = WebsiteUiState.Error(
                "Descreva a ideia do jogo que deseja criar (ex.: Jogo de nave espacial ou Quiz rápido)."
            )
            return
        }
        if (!ensureAiQuotaOrShowVip()) return
        viewModelScope.launch {
            _websiteUiState.value = WebsiteUiState.Loading
            try {
                val result = aiRepository.generateHtmlGame(idea)
                consumeAiCreditOnSuccess()
                _websiteUiState.value = WebsiteUiState.Success(result)

                val entry = SavedSiteEntry(
                    id = UUID.randomUUID().toString(),
                    companyName = result.companyName,
                    niche = "Jogo HTML5 IA",
                    whatsapp = "",
                    cityState = "",
                    services = idea,
                    htmlCode = result.htmlCode,
                    createdAtMillis = System.currentTimeMillis()
                )
                _siteHistory.value = prefsStore.saveSiteToHistory(entry)
            } catch (e: CancellationException) {
                throw e
            } catch (e: TimeoutCancellationException) {
                _websiteUiState.value = WebsiteUiState.Error(
                    "Tempo limite excedido ao criar o jogo com IA. Tente novamente."
                )
            } catch (e: Exception) {
                _websiteUiState.value = WebsiteUiState.Error(
                    e.localizedMessage ?: "Não foi possível criar o jogo."
                )
            }
        }
    }

    fun loadSiteFromHistory(entry: SavedSiteEntry) {
        if (entry.niche == "Jogo HTML5 IA") {
            _isGameMode.value = true
            _gameIdeaInput.value = entry.services
        } else {
            _isGameMode.value = false
            _websiteForm.value = WebsiteFormInput(
                companyName = entry.companyName,
                niche = entry.niche,
                whatsapp = entry.whatsapp,
                cityState = entry.cityState,
                services = entry.services
            )
        }
        _websiteUiState.value = WebsiteUiState.Success(
            GeneratedWebsite(
                companyName = entry.companyName,
                htmlCode = entry.htmlCode,
                summary = "Carregado do histórico local."
            )
        )
    }

    fun deleteSiteFromHistory(id: String) {
        _siteHistory.value = prefsStore.removeSiteFromHistory(id)
    }

    fun updateUrlInput(url: String) {
        _urlInput.value = url
    }

    fun analyzeUrl() {
        val target = _urlInput.value.trim()
        if (target.length < 4) {
            _antivirusUiState.value = AntivirusUiState.Error(
                "Cole ou digite um link, Pix Copia e Cola ou boleto válido para analisar."
            )
            return
        }
        if (!ensureAiQuotaOrShowVip()) return
        viewModelScope.launch {
            _antivirusUiState.value = AntivirusUiState.Loading
            try {
                val analysis = aiRepository.analyzeUrl(target, corporateMode = false)
                consumeAiCreditOnSuccess()
                _antivirusUiState.value = AntivirusUiState.Success(analysis)
            } catch (e: CancellationException) {
                throw e
            } catch (e: TimeoutCancellationException) {
                _antivirusUiState.value = AntivirusUiState.Error(
                    "Tempo limite excedido ao analisar o link. Tente novamente."
                )
            } catch (e: Exception) {
                _antivirusUiState.value = AntivirusUiState.Error(
                    e.localizedMessage ?: "Erro ao analisar o link."
                )
            }
        }
    }

    // Tab 3: Commercial Copy Generator
    fun selectCommercialChannel(channel: CommercialChannel) {
        _commercialChannel.value = channel
    }

    fun updateCommercialBusiness(value: String) {
        _commercialBusiness.value = value
    }

    fun updateCommercialTopic(value: String) {
        _commercialTopic.value = value
    }

    fun generateCommercialCopy() {
        val topic = _commercialTopic.value.trim()
        if (topic.isEmpty()) {
            _commercialCopyUiState.value = CommercialCopyUiState.Error(
                "Descreva o objetivo do texto ou proposta comercial para gerar."
            )
            return
        }
        viewModelScope.launch {
            _commercialCopyUiState.value = CommercialCopyUiState.Loading
            try {
                val result = aiRepository.generateCommercialCopy(
                    channel = _commercialChannel.value,
                    topic = topic,
                    businessContext = _commercialBusiness.value
                )
                _commercialCopyUiState.value = CommercialCopyUiState.Success(result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: TimeoutCancellationException) {
                _commercialCopyUiState.value = CommercialCopyUiState.Error(
                    "Tempo limite excedido ao gerar texto comercial. Tente novamente."
                )
            } catch (e: Exception) {
                _commercialCopyUiState.value = CommercialCopyUiState.Error(
                    e.localizedMessage ?: "Erro ao gerar texto comercial."
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: ToolCategory) {
        _selectedCategory.value = category
    }

    fun toggleFavoritesOnly() {
        _favoritesOnly.value = !_favoritesOnly.value
    }

    fun toggleFavorite(toolId: String) {
        val updated = _favoriteIds.value.toMutableSet()
        if (updated.contains(toolId)) {
            updated.remove(toolId)
        } else {
            updated.add(toolId)
        }
        _favoriteIds.value = updated
        prefsStore.saveFavoriteToolIds(updated)
    }

    fun openToolAssistant(tool: AiTool) {
        _activeAssistantTool.value = tool
        _assistantQuestion.value = ""
        _assistantUiState.value = AssistantUiState.Idle
    }

    fun closeToolAssistant() {
        _activeAssistantTool.value = null
        _assistantUiState.value = AssistantUiState.Idle
    }

    fun updateAssistantQuestion(question: String) {
        _assistantQuestion.value = question
    }

    fun askToolAssistant(customQuestion: String? = null) {
        val tool = _activeAssistantTool.value ?: return
        val promptQuestion = (customQuestion ?: _assistantQuestion.value).trim()
            .ifEmpty { "Como usar ${tool.name} passo a passo grátis?" }
        _assistantQuestion.value = promptQuestion

        viewModelScope.launch {
            _assistantUiState.value = AssistantUiState.Loading
            try {
                val response = aiRepository.askToolAssistant(tool, promptQuestion)
                _assistantUiState.value = AssistantUiState.Success(response)
            } catch (e: CancellationException) {
                throw e
            } catch (e: TimeoutCancellationException) {
                _assistantUiState.value = AssistantUiState.Error(
                    "Tempo limite excedido no assistente IA. Tente novamente."
                )
            } catch (e: Exception) {
                _assistantUiState.value = AssistantUiState.Error(
                    e.localizedMessage ?: "Erro no assistente da ferramenta."
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        alarmCountdownJob?.cancel()
        stopAlarmSoundAndVibration()
    }
}
