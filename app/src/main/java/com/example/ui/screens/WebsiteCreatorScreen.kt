package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.Web
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.AntivirusUiState
import com.example.R
import com.example.WeatherUiState
import com.example.WebsiteUiState
import com.example.data.SavedSiteEntry
import com.example.data.WebsiteFormInput
import com.example.ui.components.MicDictationButton
import com.example.ui.components.WeatherWidgetCard
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WebsiteCreatorScreen(
    weatherCityInput: String,
    weatherUiState: WeatherUiState,
    onWeatherCityChange: (String) -> Unit,
    onWeatherRefreshCity: () -> Unit,
    onWeatherUseGps: () -> Unit,
    onOpenAlarm: () -> Unit,
    onOpenCalculator: () -> Unit,
    onOpenYouTube: () -> Unit,
    onOpenMapsGps: () -> Unit,
    onOpenNexusLanding: () -> Unit,
    corporateUrlInput: String,
    corporateAntivirusState: AntivirusUiState,
    onCorporateUrlChange: (String) -> Unit,
    onCorporateVerifyClick: () -> Unit,
    isGameMode: Boolean,
    gameIdeaInput: String,
    onSetGameMode: (Boolean) -> Unit,
    onGameIdeaChange: (String) -> Unit,
    onGenerateGameClick: () -> Unit,
    formInput: WebsiteFormInput,
    uiState: WebsiteUiState,
    siteHistory: List<SavedSiteEntry>,
    onFormChange: ((WebsiteFormInput) -> WebsiteFormInput) -> Unit,
    onFillExample: () -> Unit,
    onGenerateClick: () -> Unit,
    onLoadFromHistory: (SavedSiteEntry) -> Unit,
    onDeleteHistoryEntry: (String) -> Unit,
    onDictateField: (label: String, onResult: (String) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    var showCodeView by remember { mutableStateOf(false) }
    var isFullscreenPreview by remember { mutableStateOf(false) }

    val pendingHtmlToSave = (uiState as? WebsiteUiState.Success)?.website?.htmlCode

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/html")
    ) { uri: Uri? ->
        if (uri != null && pendingHtmlToSave != null) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(pendingHtmlToSave.toByteArray(Charsets.UTF_8))
                }
            }.onSuccess {
                Toast.makeText(
                    context,
                    "Código HTML salvo com sucesso no celular!",
                    Toast.LENGTH_LONG
                ).show()
            }.onFailure { err ->
                Toast.makeText(
                    context,
                    "Erro ao salvar arquivo: ${err.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // =========================================================================
            // 1. WIDGET COMPACTO DE PREVISÃO DO TEMPO AO VIVO (OPEN-METEO) NO TOPO
            // =========================================================================
            WeatherWidgetCard(
                cityInput = weatherCityInput,
                weatherUiState = weatherUiState,
                onCityChange = onWeatherCityChange,
                onRefreshCity = onWeatherRefreshCity,
                onUseGps = onWeatherUseGps,
                onMicClick = {
                    onDictateField("Diga o nome da cidade") { spoken ->
                        onWeatherCityChange(spoken)
                        onWeatherRefreshCity()
                    }
                }
            )

            // =========================================================================
            // 2. BARRA RÁPIDA DE UTILITÁRIOS NATIVOS
            // =========================================================================
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    UtilityQuickPill(
                        label = "Despertador",
                        icon = Icons.Filled.Alarm,
                        accent = NexusNeonBlue,
                        onClick = onOpenAlarm,
                        tag = "utility_alarm_button"
                    )
                }
                item {
                    UtilityQuickPill(
                        label = "Calculadora Voz",
                        icon = Icons.Filled.Calculate,
                        accent = NexusEmerald,
                        onClick = onOpenCalculator,
                        tag = "utility_calculator_button"
                    )
                }
                item {
                    UtilityQuickPill(
                        label = "YouTube",
                        icon = Icons.Filled.PlayCircle,
                        accent = NexusDangerRed,
                        onClick = onOpenYouTube,
                        tag = "utility_youtube_button"
                    )
                }
                item {
                    UtilityQuickPill(
                        label = "Landing Nexus IA",
                        icon = Icons.Filled.AutoAwesome,
                        accent = NexusEmerald,
                        onClick = onOpenNexusLanding,
                        tag = "utility_nexus_landing_button"
                    )
                }
                item {
                    UtilityQuickPill(
                        label = "GPS / Mapa",
                        icon = Icons.Filled.Map,
                        accent = NexusNeonBlue,
                        onClick = onOpenMapsGps,
                        tag = "utility_gps_button"
                    )
                }
            }

            // =========================================================================
            // 3. CARTÃO DE DESTAQUE: AI ANTIVÍRUS CORPORATIVO (URL / PIX / BOLETO)
            // =========================================================================
            CorporateAntivirusSpotlightCard(
                urlInput = corporateUrlInput,
                uiState = corporateAntivirusState,
                onUrlChange = onCorporateUrlChange,
                onVerifyClick = onCorporateVerifyClick,
                onMicClick = {
                    onDictateField("Dite ou cole o link ou Pix") { spoken ->
                        onCorporateUrlChange(spoken)
                    }
                }
            )

            // =========================================================================
            // 4. CRIADOR DE SITES IA / CRIADOR DE JOGOS IA
            // =========================================================================
            Card(
                colors = CardDefaults.cardColors(containerColor = NexusSurface),
                border = BorderStroke(1.dp, NexusBorder),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    NexusNeonBlue.copy(alpha = 0.12f),
                                    NexusEmerald.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isGameMode) "Criador de Jogos IA" else "Criador de Sites IA",
                                style = MaterialTheme.typography.headlineMedium,
                                color = NexusTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isGameMode) {
                                    "Descreva um jogo e gere um game HTML5 jogável na tela gratuitamente com IA."
                                } else {
                                    "Preencha os dados da empresa e gere um site HTML/CSS completo e responsivo grátis com IA."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTextSecondary
                            )
                        }
                        if (!isGameMode) {
                            TextButton(
                                onClick = onFillExample,
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Text(
                                    text = "Exemplo",
                                    color = NexusNeonBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Mode switcher: Criador de Sites IA vs Criador de Jogos IA
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NexusBgDeep)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PreviewModeTabButton(
                            selected = !isGameMode,
                            label = "Sites IA",
                            icon = Icons.Outlined.Web,
                            onClick = { onSetGameMode(false) },
                            modifier = Modifier.weight(1f)
                        )
                        PreviewModeTabButton(
                            selected = isGameMode,
                            label = "Jogos IA",
                            icon = Icons.Filled.SportsEsports,
                            onClick = { onSetGameMode(true) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenNexusLanding,
                        border = BorderStroke(1.dp, NexusEmerald.copy(alpha = 0.65f)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("open_nexus_landing_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = NexusEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ver Modelo Pronto: Landing Page Nexus IA",
                            style = MaterialTheme.typography.labelLarge,
                            color = NexusEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Form Card (Site Mode vs Game Mode)
            Card(
                colors = CardDefaults.cardColors(containerColor = NexusSurface),
                border = BorderStroke(1.dp, NexusBorder),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isGameMode) {
                        NexusFormField(
                            value = gameIdeaInput,
                            onValueChange = onGameIdeaChange,
                            label = "Ideia do Jogo HTML5",
                            placeholder = "Ex.: Jogo de nave desviando de asteroides com pontuação",
                            icon = Icons.Filled.SportsEsports,
                            singleLine = false,
                            minLines = 3,
                            imeAction = ImeAction.Done,
                            testTag = "game_idea_input",
                            onMicClick = {
                                onDictateField("Descreva a ideia do jogo") { spoken ->
                                    onGameIdeaChange(spoken)
                                }
                            }
                        )

                        Button(
                            onClick = onGenerateGameClick,
                            enabled = uiState !is WebsiteUiState.Loading,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NexusNeonBlue,
                                contentColor = Color(0xFF04131E),
                                disabledContainerColor = NexusNeonBlue.copy(alpha = 0.45f)
                            ),
                            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("generate_game_button")
                        ) {
                            if (uiState is WebsiteUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.5.dp,
                                    color = Color(0xFF04131E)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Criando Jogo com IA...",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.SportsEsports,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Gerar Jogo com IA Grátis Agora",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        NexusFormField(
                            value = formInput.companyName,
                            onValueChange = { v -> onFormChange { it.copy(companyName = v) } },
                            label = "Nome da Empresa",
                            placeholder = "Ex.: Studio Bella Estética",
                            icon = Icons.Outlined.Apartment,
                            testTag = "company_name_input",
                            onMicClick = {
                                onDictateField("Diga o nome da empresa") { spoken ->
                                    onFormChange { it.copy(companyName = spoken) }
                                }
                            }
                        )

                        NexusFormField(
                            value = formInput.niche,
                            onValueChange = { v -> onFormChange { it.copy(niche = v) } },
                            label = "Ramo / Especialidade",
                            placeholder = "Ex.: Clínica Odontológica, Oficina Mecânica",
                            icon = Icons.Outlined.Category,
                            testTag = "niche_input",
                            onMicClick = {
                                onDictateField("Diga o ramo da empresa") { spoken ->
                                    onFormChange { it.copy(niche = spoken) }
                                }
                            }
                        )

                        NexusFormField(
                            value = formInput.whatsapp,
                            onValueChange = { v -> onFormChange { it.copy(whatsapp = v) } },
                            label = "WhatsApp com DDD",
                            placeholder = "Ex.: (11) 99999-9999",
                            icon = Icons.Outlined.Phone,
                            keyboardType = KeyboardType.Phone,
                            testTag = "whatsapp_input",
                            onMicClick = {
                                onDictateField("Diga o número de WhatsApp") { spoken ->
                                    onFormChange { it.copy(whatsapp = spoken) }
                                }
                            }
                        )

                        NexusFormField(
                            value = formInput.cityState,
                            onValueChange = { v -> onFormChange { it.copy(cityState = v) } },
                            label = "Cidade / Estado",
                            placeholder = "Ex.: Belo Horizonte - MG",
                            icon = Icons.Outlined.LocationOn,
                            testTag = "city_state_input",
                            onMicClick = {
                                onDictateField("Diga a cidade e estado") { spoken ->
                                    onFormChange { it.copy(cityState = spoken) }
                                }
                            }
                        )

                        NexusFormField(
                            value = formInput.services,
                            onValueChange = { v -> onFormChange { it.copy(services = v) } },
                            label = "Serviços / Diferenciais",
                            placeholder = "Separe por vírgula. Ex.: Atendimento 24h, Orçamento grátis, Garantia total",
                            icon = Icons.Outlined.StarOutline,
                            singleLine = false,
                            minLines = 3,
                            imeAction = ImeAction.Done,
                            testTag = "services_input",
                            onMicClick = {
                                onDictateField("Diga os serviços e diferenciais") { spoken ->
                                    onFormChange { it.copy(services = spoken) }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = onGenerateClick,
                            enabled = uiState !is WebsiteUiState.Loading,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NexusNeonBlue,
                                contentColor = Color(0xFF04131E),
                                disabledContainerColor = NexusNeonBlue.copy(alpha = 0.45f)
                            ),
                            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("generate_site_button")
                        ) {
                            if (uiState is WebsiteUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.5.dp,
                                    color = Color(0xFF04131E)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Gerando Site com IA...",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = stringResource(R.string.generate_site_cta),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Error State
            AnimatedVisibility(visible = uiState is WebsiteUiState.Error) {
                val err = uiState as? WebsiteUiState.Error
                ErrorActionCard(
                    message = err?.message.orEmpty()
                )
            }

            // Generated Output: Preview + Code + Side-by-side Copy & Download
            if (uiState is WebsiteUiState.Success) {
                val generated = uiState.website

                Card(
                    colors = CardDefaults.cardColors(containerColor = NexusSurface),
                    border = BorderStroke(1.dp, NexusEmerald.copy(alpha = 0.45f)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pronto: ${generated.companyName}",
                                style = MaterialTheme.typography.titleMedium,
                                color = NexusEmerald,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { isFullscreenPreview = true },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Fullscreen,
                                    contentDescription = "Expandir pré-visualização",
                                    tint = NexusNeonBlue
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(NexusBgDeep)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PreviewModeTabButton(
                                selected = !showCodeView,
                                label = "Pré-visualização",
                                icon = Icons.Outlined.Web,
                                onClick = { showCodeView = false },
                                modifier = Modifier.weight(1f)
                            )
                            PreviewModeTabButton(
                                selected = showCodeView,
                                label = "Código HTML/CSS",
                                icon = Icons.Outlined.Code,
                                onClick = { showCodeView = true },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (!showCodeView) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(380.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.dp, NexusBorder, RoundedCornerShape(14.dp))
                                    .testTag("website_webview_preview")
                            ) {
                                HtmlPreviewWebView(
                                    htmlCode = generated.htmlCode,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(320.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(NexusBgDeep)
                                    .border(1.dp, NexusBorder, RoundedCornerShape(14.dp))
                                    .padding(12.dp)
                                    .horizontalScroll(rememberScrollState())
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = generated.htmlCode,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp,
                                    color = NexusNeonBlue.copy(alpha = 0.92f)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    clipboard.setText(AnnotatedString(generated.htmlCode))
                                    Toast.makeText(
                                        context,
                                        "Código HTML copiado!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, NexusNeonBlue),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .testTag("copy_html_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ContentCopy,
                                    contentDescription = null,
                                    tint = NexusNeonBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Copiar Código HTML",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = NexusNeonBlue,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Button(
                                onClick = {
                                    val slug = generated.companyName
                                        .lowercase()
                                        .replace(Regex("[^a-z0-9]+"), "-")
                                        .trim('-')
                                        .ifEmpty { "meu-site" }
                                    createDocumentLauncher.launch("site-$slug.html")
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NexusEmerald,
                                    contentColor = Color(0xFF031F16)
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .testTag("download_html_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Download,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.download_html_cta),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "HTML - ${generated.companyName}")
                                    putExtra(Intent.EXTRA_TEXT, generated.htmlCode)
                                }
                                context.startActivity(
                                    Intent.createChooser(shareIntent, "Compartilhar Código HTML")
                                )
                            },
                            border = BorderStroke(1.dp, NexusBorder),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = null,
                                tint = NexusNeonBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Compartilhar Código",
                                color = NexusTextPrimary
                            )
                        }
                    }
                }
            }

            if (siteHistory.isNotEmpty()) {
                SiteHistoryCard(
                    entries = siteHistory,
                    onSelectEntry = onLoadFromHistory,
                    onDeleteEntry = onDeleteHistoryEntry
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (isFullscreenPreview && uiState is WebsiteUiState.Success) {
        Dialog(
            onDismissRequest = { isFullscreenPreview = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            BackHandler { isFullscreenPreview = false }
            Surface(
                color = NexusBgDeep,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NexusSurface)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Prévia: ${uiState.website.companyName}",
                            style = MaterialTheme.typography.titleMedium,
                            color = NexusTextPrimary
                        )
                        IconButton(
                            onClick = { isFullscreenPreview = false },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Fechar tela cheia",
                                tint = NexusNeonBlue
                            )
                        }
                    }
                    HtmlPreviewWebView(
                        htmlCode = uiState.website.htmlCode,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun UtilityQuickPill(
    label: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(NexusSurface)
            .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = accent,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = NexusTextPrimary,
            maxLines = 1
        )
    }
}

@Composable
private fun SiteHistoryCard(
    entries: List<SavedSiteEntry>,
    onSelectEntry: (SavedSiteEntry) -> Unit,
    onDeleteEntry: (String) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM HH:mm", Locale("pt", "BR")) }

    Card(
        colors = CardDefaults.cardColors(containerColor = NexusSurface),
        border = BorderStroke(1.dp, NexusBorder),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("site_history_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = null,
                    tint = NexusNeonBlue,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Histórico Recente (Últimos ${entries.size}/5 Sites)",
                    style = MaterialTheme.typography.titleMedium,
                    color = NexusTextPrimary
                )
            }
            Text(
                text = "Toque em qualquer item salvo para abrir a prévia, copiar ou baixar o HTML novamente.",
                style = MaterialTheme.typography.bodySmall,
                color = NexusTextSecondary
            )

            entries.forEach { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NexusBgDeep)
                        .border(1.dp, NexusBorder, RoundedCornerShape(12.dp))
                        .clickable { onSelectEntry(entry) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.companyName,
                            style = MaterialTheme.typography.titleSmall,
                            color = NexusTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${entry.niche} • ${dateFormatter.format(Date(entry.createdAtMillis))}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusNeonBlue.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        onClick = { onDeleteEntry(entry.id) },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Remover ${entry.companyName} do histórico",
                            tint = NexusTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CorporateAntivirusSpotlightCard(
    urlInput: String,
    uiState: AntivirusUiState,
    onUrlChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onMicClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    Card(
        colors = CardDefaults.cardColors(containerColor = NexusSurface),
        border = BorderStroke(1.5.dp, NexusNeonBlue),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("corporate_antivirus_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            NexusNeonBlue.copy(alpha = 0.14f),
                            NexusBgDeep.copy(alpha = 0.4f)
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(NexusEmerald.copy(alpha = 0.16f))
                    .border(1.dp, NexusEmerald.copy(alpha = 0.5f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.VerifiedUser,
                    contentDescription = null,
                    tint = NexusEmerald,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.corporate_av_badge),
                    style = MaterialTheme.typography.labelSmall,
                    color = NexusEmerald,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NexusNeonBlue.copy(alpha = 0.15f))
                        .border(1.dp, NexusNeonBlue.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = NexusNeonBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.corporate_av_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = NexusTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Cole URLs suspeitas, códigos \"Pix Copia e Cola\" ou linha digitável de boletos para detectar fraudes financeiras contra empresas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTextSecondary
                    )
                }
            }

            OutlinedTextField(
                value = urlInput,
                onValueChange = onUrlChange,
                label = { Text("URL, Pix Copia e Cola ou Boleto") },
                placeholder = { Text("Cole link, 000201... (Pix) ou linha digitável") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.QrCodeScanner,
                        contentDescription = null,
                        tint = NexusNeonBlue
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MicDictationButton(
                            contentDescription = "Ditar link ou código Pix",
                            onClick = onMicClick,
                            tag = "mic_corporate_av"
                        )
                        if (urlInput.isNotEmpty()) {
                            IconButton(onClick = { onUrlChange("") }) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = "Limpar campo",
                                    tint = NexusTextSecondary
                                )
                            }
                        }
                        IconButton(
                            onClick = {
                                clipboard.getText()?.text?.let { pasted ->
                                    onUrlChange(pasted.trim())
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentPaste,
                                contentDescription = "Colar conteúdo",
                                tint = NexusNeonBlue
                            )
                        }
                    }
                },
                maxLines = 3,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NexusNeonBlue,
                    unfocusedBorderColor = NexusBorder,
                    focusedContainerColor = NexusBgDeep,
                    unfocusedContainerColor = NexusBgDeep,
                    focusedTextColor = NexusTextPrimary,
                    unfocusedTextColor = NexusTextPrimary,
                    focusedLabelColor = NexusNeonBlue,
                    unfocusedLabelColor = NexusTextSecondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("corporate_url_input")
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    AssistChip(
                        onClick = { onUrlChange("https://www.bcb.gov.br/estabilidadefinanceira/pix") },
                        label = { Text("URL Segura") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = NexusBgDeep,
                            labelColor = NexusEmerald
                        ),
                        border = BorderStroke(1.dp, NexusEmerald.copy(alpha = 0.4f))
                    )
                }
                item {
                    AssistChip(
                        onClick = { onUrlChange("http://2via-boleto-corporativo-urgente.xyz/pix") },
                        label = { Text("URL Golpe") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = NexusBgDeep,
                            labelColor = NexusDangerRed
                        ),
                        border = BorderStroke(1.dp, NexusDangerRed.copy(alpha = 0.4f))
                    )
                }
                item {
                    AssistChip(
                        onClick = {
                            onUrlChange("00020126580014br.gov.bcb.pix0136pagamento-urgente-fornecedor@fakemail.xyz5204000053039865802BR5915EMPRESA TESTE6009SAO PAULO62070503***6304ABCD")
                        },
                        label = { Text("Pix Copia e Cola") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = NexusBgDeep,
                            labelColor = NexusNeonBlue
                        ),
                        border = BorderStroke(1.dp, NexusNeonBlue.copy(alpha = 0.4f))
                    )
                }
                item {
                    AssistChip(
                        onClick = { onUrlChange("23793.38128 60000.000003 00000.000400 1 95000000015000") },
                        label = { Text("Boleto") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = NexusBgDeep,
                            labelColor = NexusNeonBlue
                        ),
                        border = BorderStroke(1.dp, NexusNeonBlue.copy(alpha = 0.4f))
                    )
                }
            }

            Button(
                onClick = onVerifyClick,
                enabled = uiState !is AntivirusUiState.Loading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NexusEmerald,
                    contentColor = Color(0xFF031F16),
                    disabledContainerColor = NexusEmerald.copy(alpha = 0.45f)
                ),
                contentPadding = PaddingValues(vertical = 15.dp, horizontal = 18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("corporate_verify_link_button")
            ) {
                if (uiState is AntivirusUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.5.dp,
                        color = Color(0xFF031F16)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Analisando Risco Financeiro...",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.corporate_av_cta),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (uiState is AntivirusUiState.Error) {
                ErrorActionCard(
                    message = uiState.message
                )
            }

            if (uiState is AntivirusUiState.Success) {
                val analysis = uiState.result
                val statusColor = if (analysis.isSafe) NexusEmerald else NexusDangerRed
                val statusIcon = if (analysis.isSafe) Icons.Filled.VerifiedUser else Icons.Filled.GppBad

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(NexusBgDeep)
                        .border(1.5.dp, statusColor.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                        .testTag("corporate_antivirus_result"),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(statusColor.copy(alpha = 0.16f))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = analysis.badgeTitle,
                            tint = statusColor,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = analysis.badgeTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = statusColor,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = analysis.normalizedUrl,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = JetBrainsMonoFontFamily,
                                color = NexusTextSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Nível de Risco Corporativo",
                                style = MaterialTheme.typography.labelMedium,
                                color = NexusTextSecondary
                            )
                            Text(
                                text = "${analysis.riskScore}%",
                                style = MaterialTheme.typography.labelLarge,
                                color = statusColor
                            )
                        }
                        LinearProgressIndicator(
                            progress = { analysis.riskScore / 100f },
                            color = statusColor,
                            trackColor = NexusSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(999.dp))
                        )
                    }

                    Text(
                        text = analysis.detailedExplanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTextPrimary
                    )

                    if (analysis.indicators.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            analysis.indicators.forEach { indicator ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = if (analysis.isSafe) Icons.Outlined.CheckCircle else Icons.Outlined.WarningAmber,
                                        contentDescription = null,
                                        tint = statusColor,
                                        modifier = Modifier
                                            .padding(top = 2.dp)
                                            .size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = indicator,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NexusTextSecondary
                                    )
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            val reportText = buildString {
                                appendLine("*NEXUS AI 100 — AI Antivírus Corporativo*")
                                appendLine("Status: *${analysis.badgeTitle}* (Risco: ${analysis.riskScore}%)")
                                appendLine("Item analisado: ${analysis.normalizedUrl}")
                                appendLine()
                                appendLine(analysis.detailedExplanation)
                                if (analysis.indicators.isNotEmpty()) {
                                    appendLine()
                                    appendLine("*Sinais verificados:*")
                                    analysis.indicators.forEach { appendLine("• $it") }
                                }
                            }
                            val waUri = Uri.parse("https://wa.me/?text=${Uri.encode(reportText)}")
                            val waIntent = Intent(Intent.ACTION_VIEW, waUri)
                            runCatching {
                                context.startActivity(waIntent)
                            }.onFailure {
                                val fallback = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, reportText)
                                }
                                context.startActivity(
                                    Intent.createChooser(fallback, "Compartilhar Relatório no WhatsApp")
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NexusEmerald),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("corporate_share_whatsapp_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = null,
                            tint = NexusEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Compartilhar no WhatsApp",
                            style = MaterialTheme.typography.labelLarge,
                            color = NexusEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorActionCard(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NexusDangerRed.copy(alpha = 0.14f)),
        border = BorderStroke(1.dp, NexusDangerRed.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFFCA5A5)
            )
        }
    }
}

@Composable
private fun PreviewModeTabButton(
    selected: Boolean,
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) NexusSurfaceElevated else Color.Transparent,
            contentColor = if (selected) NexusNeonBlue else NexusTextSecondary
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp),
        contentPadding = PaddingValues(vertical = 10.dp, horizontal = 8.dp),
        modifier = modifier.height(48.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1
        )
    }
}

@Composable
private fun NexusFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    testTag: String,
    onMicClick: () -> Unit,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = NexusTextSecondary.copy(alpha = 0.6f)) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NexusNeonBlue
            )
        },
        trailingIcon = {
            MicDictationButton(
                contentDescription = "Ditar $label",
                onClick = onMicClick,
                tag = "mic_$testTag"
            )
        },
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NexusNeonBlue,
            unfocusedBorderColor = NexusBorder,
            focusedContainerColor = NexusBgDeep,
            unfocusedContainerColor = NexusBgDeep,
            focusedTextColor = NexusTextPrimary,
            unfocusedTextColor = NexusTextPrimary,
            focusedLabelColor = NexusNeonBlue,
            unfocusedLabelColor = NexusTextSecondary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun HtmlPreviewWebView(
    htmlCode: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url?.toString() ?: return false
                        if (url.startsWith("http://") || url.startsWith("https://")) {
                            runCatching {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }
                            return true
                        }
                        return false
                    }
                }
                loadDataWithBaseURL(null, htmlCode, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, htmlCode, "text/html", "UTF-8", null)
        }
    )
}
