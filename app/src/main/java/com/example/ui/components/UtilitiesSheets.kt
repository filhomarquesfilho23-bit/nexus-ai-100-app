package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.UniversalAssistantUiState
import com.example.ui.screens.ErrorActionCard
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NexusBgDeep
import com.example.ui.theme.NexusBorder
import com.example.ui.theme.NexusDangerRed
import com.example.ui.theme.NexusEmerald
import com.example.ui.theme.NexusNeonBlue
import com.example.ui.theme.NexusSurface
import com.example.ui.theme.NexusSurfaceElevated
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary
import com.example.util.MathEvaluator
import java.net.URLEncoder
import java.util.Calendar

// =============================================================================
// 1. DESPERTADOR INTEGRADO (SYSTEM ALARM + IN-APP SOUND & VIBRATION)
// =============================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmModalSheet(
    initialHour: Int?,
    initialMinute: Int?,
    initialLabel: String,
    activeInAppAlarmLabel: String?,
    activeInAppAlarmTargetTimeText: String?,
    onScheduleInAppAlarm: (hour: Int, minute: Int, label: String) -> Unit,
    onCancelInAppAlarm: () -> Unit,
    onTestRingNow: () -> Unit,
    onDictateLabel: ((String) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val cal = remember { Calendar.getInstance() }
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour ?: cal.get(Calendar.HOUR_OF_DAY),
        initialMinute = initialMinute ?: cal.get(Calendar.MINUTE),
        is24Hour = true
    )
    var alarmLabel by remember(initialLabel) {
        mutableStateOf(initialLabel.ifBlank { "Lembrete NEXUS AI 100" })
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    BackHandler { onDismiss() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NexusSurface,
        contentColor = NexusTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Alarm,
                        contentDescription = null,
                        tint = NexusNeonBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Despertador Integrado",
                        style = MaterialTheme.typography.titleLarge,
                        color = NexusTextPrimary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Fechar despertador",
                        tint = NexusTextSecondary
                    )
                }
            }

            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = NexusBgDeep,
                    selectorColor = NexusNeonBlue,
                    containerColor = NexusSurface,
                    periodSelectorBorderColor = NexusBorder,
                    clockDialSelectedContentColor = Color(0xFF04131E),
                    clockDialUnselectedContentColor = NexusTextPrimary,
                    timeSelectorSelectedContainerColor = NexusNeonBlue.copy(alpha = 0.22f),
                    timeSelectorUnselectedContainerColor = NexusBgDeep,
                    timeSelectorSelectedContentColor = NexusNeonBlue,
                    timeSelectorUnselectedContentColor = NexusTextPrimary
                )
            )

            OutlinedTextField(
                value = alarmLabel,
                onValueChange = { alarmLabel = it },
                label = { Text("Rótulo / Lembrete do Alarme") },
                placeholder = { Text("Ex.: Reunião importante") },
                singleLine = true,
                trailingIcon = {
                    MicDictationButton(
                        contentDescription = "Ditar lembrete do alarme",
                        onClick = { onDictateLabel { spoken -> alarmLabel = spoken } },
                        tag = "mic_alarm_label"
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NexusNeonBlue,
                    unfocusedBorderColor = NexusBorder,
                    focusedContainerColor = NexusBgDeep,
                    unfocusedContainerColor = NexusBgDeep,
                    focusedTextColor = NexusTextPrimary,
                    unfocusedTextColor = NexusTextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("alarm_label_input")
            )

            if (activeInAppAlarmTargetTimeText != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NexusEmerald.copy(alpha = 0.14f))
                        .border(1.dp, NexusEmerald.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AlarmOn,
                            contentDescription = null,
                            tint = NexusEmerald
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Alarme interno ativo: $activeInAppAlarmTargetTimeText",
                                style = MaterialTheme.typography.titleSmall,
                                color = NexusEmerald
                            )
                            Text(
                                text = activeInAppAlarmLabel.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTextSecondary
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = onCancelInAppAlarm,
                        border = BorderStroke(1.dp, NexusDangerRed.copy(alpha = 0.6f))
                    ) {
                        Text("Cancelar", color = NexusDangerRed)
                    }
                }
            }

            // Botão 1: Sistema Android (AlarmClock.ACTION_SET_ALARM)
            Button(
                onClick = {
                    val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                        putExtra(AlarmClock.EXTRA_HOUR, timePickerState.hour)
                        putExtra(AlarmClock.EXTRA_MINUTES, timePickerState.minute)
                        putExtra(AlarmClock.EXTRA_MESSAGE, alarmLabel.ifBlank { "NEXUS AI 100" })
                        putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                    }
                    runCatching {
                        context.startActivity(intent)
                    }.onFailure {
                        Toast.makeText(
                            context,
                            "Nenhum app de Relógio externo disponível. Ative o Alarme Interno abaixo!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NexusNeonBlue,
                    contentColor = Color(0xFF04131E)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("set_system_alarm_button")
            ) {
                Icon(Icons.Filled.Alarm, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ativar no Sistema Android", fontWeight = FontWeight.Bold)
            }

            // Botão 2: Alarme Interno (Som + Vibração)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onScheduleInAppAlarm(
                            timePickerState.hour,
                            timePickerState.minute,
                            alarmLabel.ifBlank { "Alarme NEXUS AI 100" }
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NexusEmerald,
                        contentColor = Color(0xFF031F16)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("set_inapp_alarm_button")
                ) {
                    Icon(Icons.Filled.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Alarme no App", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onTestRingNow,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, NexusBorder),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text("Testar Toque", color = NexusTextPrimary)
                }
            }
        }
    }
}

@Composable
fun AlarmRingingDialog(
    alarmLabel: String,
    onStopAlarm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onStopAlarm,
        containerColor = NexusSurface,
        titleContentColor = NexusNeonBlue,
        textContentColor = NexusTextPrimary,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.NotificationsActive,
                    contentDescription = null,
                    tint = NexusEmerald,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Alarme Tocando!")
            }
        },
        text = {
            Text(
                text = alarmLabel.ifBlank { "Seu alarme do NEXUS AI 100 disparou!" },
                style = MaterialTheme.typography.titleMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onStopAlarm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NexusDangerRed,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("PARAR ALARME", fontWeight = FontWeight.Bold)
            }
        }
    )
}

// =============================================================================
// 2. CALCULADORA INTELIGENTE QUE FALA O VALOR (TTS pt-BR)
// =============================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorModalSheet(
    initialExpression: String,
    initialResultText: String,
    isVoiceMuted: Boolean,
    onToggleMute: () -> Unit,
    onSpeakText: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var expression by remember(initialExpression) { mutableStateOf(initialExpression) }
    var displayResult by remember(initialResultText) { mutableStateOf(initialResultText) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    BackHandler { onDismiss() }

    fun evaluateAndSpeak() {
        if (expression.isBlank()) return
        val value = MathEvaluator.evaluateExpression(expression)
        if (value == null) {
            displayResult = "Expressão inválida"
            onSpeakText("Expressão inválida")
        } else {
            val formatted = MathEvaluator.formatResult(value)
            displayResult = formatted
            val spoken = MathEvaluator.formatSpokenPortuguese(value)
            onSpeakText("O resultado é $spoken")
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NexusSurface,
        contentColor = NexusTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Calculate,
                        contentDescription = null,
                        tint = NexusNeonBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Calculadora Falante",
                        style = MaterialTheme.typography.titleLarge,
                        color = NexusTextPrimary
                    )
                }

                OutlinedButton(
                    onClick = onToggleMute,
                    shape = RoundedCornerShape(999.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isVoiceMuted) NexusDangerRed.copy(alpha = 0.6f) else NexusEmerald
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .testTag("calculator_voice_toggle")
                ) {
                    Icon(
                        imageVector = if (isVoiceMuted) Icons.Filled.VolumeOff else Icons.Filled.VolumeUp,
                        contentDescription = null,
                        tint = if (isVoiceMuted) NexusDangerRed else NexusEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isVoiceMuted) "Silenciada" else "Voz Ativa",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isVoiceMuted) NexusDangerRed else NexusEmerald
                    )
                }
            }

            // Display Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NexusBgDeep)
                    .border(1.dp, NexusBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = expression.ifEmpty { "0" },
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 20.sp,
                    color = NexusTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = displayResult.ifEmpty { "0" },
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = NexusNeonBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("calculator_result_text")
                )
            }

            // Keypad Rows
            val rows = listOf(
                listOf("C", "(", ")", "÷"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "+"),
                listOf("0", ",", "%", "=")
            )

            rows.forEach { rowKeys ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowKeys.forEach { key ->
                        val isEquals = key == "="
                        val isOperator = key in listOf("÷", "×", "-", "+", "%")
                        val isClear = key == "C"

                        val bgColor = when {
                            isEquals -> NexusEmerald
                            isClear -> NexusDangerRed.copy(alpha = 0.22f)
                            isOperator -> NexusNeonBlue.copy(alpha = 0.16f)
                            else -> NexusSurfaceElevated
                        }
                        val fgColor = when {
                            isEquals -> Color(0xFF031F16)
                            isClear -> NexusDangerRed
                            isOperator -> NexusNeonBlue
                            else -> NexusTextPrimary
                        }

                        Button(
                            onClick = {
                                when (key) {
                                    "C" -> {
                                        expression = ""
                                        displayResult = "0"
                                    }
                                    "=" -> evaluateAndSpeak()
                                    else -> {
                                        expression += key
                                        val live = MathEvaluator.evaluateExpression(expression)
                                        if (live != null) {
                                            displayResult = MathEvaluator.formatResult(live)
                                        }
                                    }
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = bgColor,
                                contentColor = fgColor
                            ),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp)
                                .testTag("calc_btn_$key")
                        ) {
                            Text(
                                text = key,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Backspace row
            OutlinedButton(
                onClick = {
                    if (expression.isNotEmpty()) {
                        expression = expression.dropLast(1)
                        val live = MathEvaluator.evaluateExpression(expression)
                        displayResult = if (live != null) MathEvaluator.formatResult(live) else "0"
                    }
                },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, NexusBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Apagar último dígito",
                    tint = NexusTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Apagar Dígito", color = NexusTextSecondary)
            }
        }
    }
}

// =============================================================================
// 3. YOUTUBE BUSCA NA TELA
// =============================================================================
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeSearchSheet(
    initialQuery: String,
    onDictateQuery: ((String) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var queryDraft by remember(initialQuery) { mutableStateOf(initialQuery) }
    var activeQuery by remember(initialQuery) { mutableStateOf(initialQuery.ifBlank { "Inteligência Artificial Brasil" }) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BackHandler { onDismiss() }
        Surface(
            color = NexusBgDeep,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NexusSurface)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.PlayCircle,
                            contentDescription = null,
                            tint = NexusDangerRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "YouTube Busca",
                            style = MaterialTheme.typography.titleMedium,
                            color = NexusTextPrimary
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                val encoded = URLEncoder.encode(activeQuery, "UTF-8")
                                val uri = Uri.parse("https://www.youtube.com/results?search_query=$encoded")
                                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
                            },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.OpenInNew,
                                contentDescription = "Abrir no App YouTube",
                                tint = NexusNeonBlue
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Fechar YouTube",
                                tint = NexusTextPrimary
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NexusSurface)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = queryDraft,
                        onValueChange = { queryDraft = it },
                        placeholder = { Text("Buscar vídeos no YouTube...") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Outlined.Search, contentDescription = null, tint = NexusNeonBlue)
                        },
                        trailingIcon = {
                            MicDictationButton(
                                contentDescription = "Ditar busca do YouTube",
                                onClick = {
                                    onDictateQuery { spoken ->
                                        queryDraft = spoken
                                        activeQuery = spoken
                                    }
                                },
                                tag = "mic_youtube_search"
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            if (queryDraft.isNotBlank()) activeQuery = queryDraft.trim()
                        }),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NexusNeonBlue,
                            unfocusedBorderColor = NexusBorder,
                            focusedContainerColor = NexusBgDeep,
                            unfocusedContainerColor = NexusBgDeep,
                            focusedTextColor = NexusTextPrimary,
                            unfocusedTextColor = NexusTextPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("youtube_search_input")
                    )

                    Button(
                        onClick = { if (queryDraft.isNotBlank()) activeQuery = queryDraft.trim() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NexusNeonBlue,
                            contentColor = Color(0xFF04131E)
                        ),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Text("Ir", fontWeight = FontWeight.Bold)
                    }
                }

                val ytUrl = remember(activeQuery) {
                    "https://m.youtube.com/results?search_query=${URLEncoder.encode(activeQuery, "UTF-8")}"
                }

                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean = false
                            }
                            loadUrl(ytUrl)
                        }
                    },
                    update = { webView ->
                        if (webView.url != ytUrl) {
                            webView.loadUrl(ytUrl)
                        }
                    }
                )
            }
        }
    }
}

// =============================================================================
// 4. ASSISTENTE UNIVERSAL POR VOZ (MODAL DE RESULTADO, PERGUNTA E COMANDOS)
// =============================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalVoiceAssistantSheet(
    isListening: Boolean,
    isSpeaking: Boolean,
    isVoiceMuted: Boolean,
    recognizedQuery: String,
    uiState: UniversalAssistantUiState,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onSubmitTextCommand: (String) -> Unit,
    onToggleMute: () -> Unit,
    onSpeakAgain: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var manualCommand by remember(recognizedQuery) { mutableStateOf(recognizedQuery) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    BackHandler { onDismiss() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NexusSurface,
        contentColor = NexusTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isListening) Icons.Filled.GraphicEq else Icons.Filled.Mic,
                        contentDescription = null,
                        tint = NexusNeonBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Assistente Universal por Voz",
                        style = MaterialTheme.typography.titleMedium,
                        color = NexusTextPrimary
                    )
                }

                OutlinedButton(
                    onClick = onToggleMute,
                    shape = RoundedCornerShape(999.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isVoiceMuted) NexusDangerRed.copy(alpha = 0.6f) else NexusEmerald
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(
                        imageVector = if (isVoiceMuted) Icons.Filled.VolumeOff else Icons.Filled.VolumeUp,
                        contentDescription = null,
                        tint = if (isVoiceMuted) NexusDangerRed else NexusEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isVoiceMuted) "Silenciado" else if (isSpeaking) "Falando..." else "Voz Ativa",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isVoiceMuted) NexusDangerRed else NexusEmerald
                    )
                }
            }

            // Big Tap-to-Speak Button
            Button(
                onClick = { if (isListening) onStopListening() else onStartListening() },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isListening) NexusDangerRed else NexusNeonBlue,
                    contentColor = if (isListening) Color.White else Color(0xFF04131E)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("voice_assistant_speak_button")
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Filled.GraphicEq else Icons.Filled.Mic,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isListening) "Ouvindo em Português... (Toque p/ parar)" else "Falar Comando ou Pergunta",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Quick example commands
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val samples = listOf(
                    "Criar site de barbearia moderna",
                    "Criar jogo de corrida espacial",
                    "YouTube música para foco",
                    "Como chegar em Floriano PI",
                    "Alarme 7 e 30",
                    "Quanto é 150 vezes 4"
                )
                items(samples) { cmd ->
                    AssistChip(
                        onClick = {
                            manualCommand = cmd
                            onSubmitTextCommand(cmd)
                        },
                        label = { Text(cmd) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = NexusBgDeep,
                            labelColor = NexusNeonBlue
                        ),
                        border = BorderStroke(1.dp, NexusBorder)
                    )
                }
            }

            // Text fallback + Mic icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = manualCommand,
                    onValueChange = { manualCommand = it },
                    label = { Text("Ou digite sua pergunta / comando") },
                    singleLine = true,
                    trailingIcon = {
                        MicDictationButton(
                            contentDescription = "Falar comando",
                            onClick = onStartListening,
                            tag = "mic_universal_input"
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (manualCommand.isNotBlank()) onSubmitTextCommand(manualCommand)
                    }),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NexusNeonBlue,
                        unfocusedBorderColor = NexusBorder,
                        focusedContainerColor = NexusBgDeep,
                        unfocusedContainerColor = NexusBgDeep,
                        focusedTextColor = NexusTextPrimary,
                        unfocusedTextColor = NexusTextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        if (manualCommand.isNotBlank()) onSubmitTextCommand(manualCommand)
                    },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NexusEmerald)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Executar comando",
                        tint = Color(0xFF031F16)
                    )
                }
            }

            when (uiState) {
                is UniversalAssistantUiState.Idle -> {
                    Text(
                        text = "Exemplos: \"criar site...\", \"criar jogo...\", \"youtube...\", \"como chegar em...\", \"alarme...\", \"quanto é 150 vezes 4\" ou qualquer pergunta para a IA.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTextSecondary
                    )
                }

                is UniversalAssistantUiState.Loading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp,
                            color = NexusNeonBlue
                        )
                        Text(
                            text = "Consultando a IA...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexusNeonBlue
                        )
                    }
                }

                is UniversalAssistantUiState.Error -> {
                    ErrorActionCard(
                        message = uiState.message
                    )
                }

                is UniversalAssistantUiState.Answer -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NexusBgDeep),
                        border = BorderStroke(1.dp, NexusNeonBlue.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = null,
                                        tint = NexusEmerald,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = uiState.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = NexusEmerald
                                    )
                                }
                                OutlinedButton(
                                    onClick = { onSpeakAgain(uiState.body) },
                                    border = BorderStroke(1.dp, NexusNeonBlue),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.VolumeUp,
                                        contentDescription = null,
                                        tint = NexusNeonBlue,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ouvir", color = NexusNeonBlue, fontSize = 12.sp)
                                }
                            }
                            Text(
                                text = uiState.body,
                                style = MaterialTheme.typography.bodyMedium,
                                color = NexusTextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
