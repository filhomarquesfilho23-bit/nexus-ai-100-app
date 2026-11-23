package com.example

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Web
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.ToolsCatalog
import com.example.ui.components.AdsterraBottomBanner
import com.example.ui.components.AdsterraTopBanner
import com.example.ui.components.AlarmModalSheet
import com.example.ui.components.AlarmRingingDialog
import com.example.ui.components.CalculatorModalSheet
import com.example.ui.components.NexusLandingPageSheet
import com.example.ui.components.NexusTopBar
import com.example.ui.components.UniversalVoiceAssistantSheet
import com.example.ui.components.VipUnlockDialog
import com.example.ui.components.YouTubeSearchSheet
import com.example.ui.screens.AiAntivirusScreen
import com.example.ui.screens.ToolsCatalogScreen
import com.example.ui.screens.WebsiteCreatorScreen
import com.example.ui.theme.NexusAiTheme
import com.example.ui.theme.NexusBgDeep
import com.example.ui.theme.NexusBorder
import com.example.ui.theme.NexusNeonBlue
import com.example.ui.theme.NexusSurface
import com.example.ui.theme.NexusTextSecondary
import com.example.util.VoiceAndTtsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val darkArgb = NexusBgDeep.toArgb()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(darkArgb),
            navigationBarStyle = SystemBarStyle.dark(darkArgb)
        )
        setContent {
            NexusAiTheme {
                NexusApp()
            }
        }
    }
}

@Composable
fun NexusApp(
    viewModel: NexusViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val voiceManager = remember { VoiceAndTtsManager(context.applicationContext) }

    DisposableEffect(voiceManager) {
        onDispose {
            voiceManager.release()
        }
    }

    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val isVipUnlocked by viewModel.isVipUnlocked.collectAsStateWithLifecycle()
    val remainingFreeUsesToday by viewModel.remainingFreeUsesToday.collectAsStateWithLifecycle()
    val isVipDialogOpen by viewModel.isVipDialogOpen.collectAsStateWithLifecycle()
    val vipActivationError by viewModel.vipActivationError.collectAsStateWithLifecycle()

    val weatherCityInput by viewModel.weatherCityInput.collectAsStateWithLifecycle()
    val weatherUiState by viewModel.weatherUiState.collectAsStateWithLifecycle()

    val isAlarmSheetOpen by viewModel.isAlarmSheetOpen.collectAsStateWithLifecycle()
    val alarmInitialHour by viewModel.alarmInitialHour.collectAsStateWithLifecycle()
    val alarmInitialMinute by viewModel.alarmInitialMinute.collectAsStateWithLifecycle()
    val alarmInitialLabel by viewModel.alarmInitialLabel.collectAsStateWithLifecycle()
    val activeInAppAlarmLabel by viewModel.activeInAppAlarmLabel.collectAsStateWithLifecycle()
    val activeInAppAlarmTimeText by viewModel.activeInAppAlarmTimeText.collectAsStateWithLifecycle()
    val ringingAlarmLabel by viewModel.ringingAlarmLabel.collectAsStateWithLifecycle()

    val isCalculatorOpen by viewModel.isCalculatorOpen.collectAsStateWithLifecycle()
    val calcInitialExpression by viewModel.calcInitialExpression.collectAsStateWithLifecycle()
    val calcInitialResult by viewModel.calcInitialResult.collectAsStateWithLifecycle()

    val youtubeQueryOpen by viewModel.youtubeQueryOpen.collectAsStateWithLifecycle()
    val isNexusLandingOpen by viewModel.isNexusLandingOpen.collectAsStateWithLifecycle()

    val isVoiceSheetOpen by viewModel.isVoiceSheetOpen.collectAsStateWithLifecycle()
    val recognizedVoiceText by viewModel.recognizedVoiceText.collectAsStateWithLifecycle()
    val universalAssistantUiState by viewModel.universalAssistantUiState.collectAsStateWithLifecycle()

    val isListeningVoice by voiceManager.isListening.collectAsStateWithLifecycle()
    val isSpeakingVoice by voiceManager.isSpeaking.collectAsStateWithLifecycle()
    val isVoiceMuted by voiceManager.isVoiceMuted.collectAsStateWithLifecycle()

    val corporateUrlInput by viewModel.corporateUrlInput.collectAsStateWithLifecycle()
    val corporateAntivirusState by viewModel.corporateAntivirusUiState.collectAsStateWithLifecycle()

    val isGameMode by viewModel.isGameMode.collectAsStateWithLifecycle()
    val gameIdeaInput by viewModel.gameIdeaInput.collectAsStateWithLifecycle()
    val websiteForm by viewModel.websiteForm.collectAsStateWithLifecycle()
    val websiteUiState by viewModel.websiteUiState.collectAsStateWithLifecycle()
    val siteHistory by viewModel.siteHistory.collectAsStateWithLifecycle()

    val urlInput by viewModel.urlInput.collectAsStateWithLifecycle()
    val antivirusUiState by viewModel.antivirusUiState.collectAsStateWithLifecycle()

    val commercialChannel by viewModel.commercialChannel.collectAsStateWithLifecycle()
    val commercialBusiness by viewModel.commercialBusiness.collectAsStateWithLifecycle()
    val commercialTopic by viewModel.commercialTopic.collectAsStateWithLifecycle()
    val commercialCopyUiState by viewModel.commercialCopyUiState.collectAsStateWithLifecycle()

    val filteredTools by viewModel.filteredTools.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val favoritesOnly by viewModel.favoritesOnly.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val activeAssistantTool by viewModel.activeAssistantTool.collectAsStateWithLifecycle()
    val assistantQuestion by viewModel.assistantQuestion.collectAsStateWithLifecycle()
    val assistantUiState by viewModel.assistantUiState.collectAsStateWithLifecycle()

    fun openMapsGps(destination: String) {
        val query = destination.trim().ifEmpty { weatherCityInput }
        val geoIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(query)}"))
        runCatching {
            context.startActivity(geoIntent)
        }.onFailure {
            val webMap = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}")
            )
            runCatching { context.startActivity(webMap) }
        }
    }

    // Pending speech callback for both inline SpeechRecognizer and Activity fallback
    var pendingSpeechCallback by remember { mutableStateOf<((String) -> Unit)?>(null) }
    var pendingSpeechPrompt by remember { mutableStateOf("Fale em português...") }

    val speechActivityFallbackLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
                ?.trim()
                .orEmpty()
            if (spoken.isNotEmpty()) {
                pendingSpeechCallback?.invoke(spoken)
            }
        }
        pendingSpeechCallback = null
    }

    fun launchSpeechCaptureNow(prompt: String, onText: (String) -> Unit) {
        if (voiceManager.isInlineSpeechAvailable()) {
            pendingSpeechCallback = null
            voiceManager.startInlineListening(
                label = prompt,
                onResult = { spoken -> onText(spoken) },
                onError = { errMsg ->
                    Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            pendingSpeechCallback = onText
            runCatching {
                speechActivityFallbackLauncher.launch(voiceManager.createSpeechIntent(prompt))
            }.onFailure {
                pendingSpeechCallback = null
                Toast.makeText(
                    context,
                    "Serviço de voz indisponível. Digite o comando no campo de texto.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        val cb = pendingSpeechCallback
        if (granted && cb != null) {
            launchSpeechCaptureNow(pendingSpeechPrompt, cb)
        } else if (!granted) {
            pendingSpeechCallback = null
            Toast.makeText(
                context,
                "Permissão de microfone necessária para ditar ou usar comandos de voz.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun startDictation(prompt: String, onResult: (String) -> Unit) {
        val hasAudio = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasAudio) {
            launchSpeechCaptureNow(prompt, onResult)
        } else {
            pendingSpeechPrompt = prompt
            pendingSpeechCallback = onResult
            recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun triggerUniversalVoiceCommand() {
        viewModel.openVoiceAssistantSheet()
        startDictation("Fale qualquer pergunta ou comando...") { spoken ->
            viewModel.routeVoiceCommand(
                spokenText = spoken,
                onSpeakResponse = { reply -> voiceManager.speak(reply) },
                onOpenMapsGps = ::openMapsGps
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun resolveGpsAndFetchWeather() {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (lm == null) {
            viewModel.refreshWeatherByCity()
            return
        }
        val providers = listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)
        val location = providers.firstNotNullOfOrNull { provider ->
            runCatching { lm.getLastKnownLocation(provider) }.getOrNull()
        }
        if (location == null) {
            Toast.makeText(
                context,
                "Sinal GPS indisponível no momento; exibindo clima da cidade digitada.",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.refreshWeatherByCity()
            return
        }

        scope.launch {
            val resolvedName = withContext(Dispatchers.IO) {
                runCatching {
                    @Suppress("DEPRECATION")
                    val addresses = Geocoder(context, Locale("pt", "BR"))
                        .getFromLocation(location.latitude, location.longitude, 1)
                    val addr = addresses?.firstOrNull()
                    val city = addr?.locality ?: addr?.subAdminArea
                    val state = addr?.adminArea
                    when {
                        !city.isNullOrBlank() && !state.isNullOrBlank() -> "$city - $state"
                        !city.isNullOrBlank() -> city
                        else -> null
                    }
                }.getOrNull()
            }
            viewModel.fetchWeatherByGps(location.latitude, location.longitude, resolvedName)
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            resolveGpsAndFetchWeather()
        } else {
            Toast.makeText(
                context,
                "Permissão de localização negada. Digite a cidade manualmente para ver o clima.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun requestGpsWeather() {
        val hasCoarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasCoarse) {
            resolveGpsAndFetchWeather()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    BackHandler(enabled = selectedTab != 0) {
        viewModel.selectTab(0)
    }

    Scaffold(
        containerColor = NexusBgDeep,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                NexusTopBar(
                    isListeningVoice = isListeningVoice,
                    isVipUnlocked = isVipUnlocked,
                    remainingFreeUsesToday = remainingFreeUsesToday,
                    onOpenVipDialog = viewModel::openVipDialog,
                    onOpenVoiceAssistant = ::triggerUniversalVoiceCommand
                )
                HorizontalDivider(color = NexusBorder, thickness = 1.dp)
                // <!-- ESPAÇO BANNER ADSTERRA TOPO -->
                AdsterraTopBanner()
            }
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // <!-- ESPAÇO BANNER ADSTERRA RODAPE -->
                AdsterraBottomBanner()
                HorizontalDivider(color = NexusBorder, thickness = 1.dp)
                NavigationBar(
                    containerColor = NexusSurface,
                    tonalElevation = 0.dp
                ) {
                    val tabItems = listOf(
                        Triple(
                            stringResource(R.string.tab_site_creator),
                            Icons.Filled.Web to Icons.Outlined.Web,
                            "tab_site_creator"
                        ),
                        Triple(
                            stringResource(R.string.tab_antivirus),
                            Icons.Filled.Security to Icons.Outlined.Security,
                            "tab_antivirus"
                        ),
                        Triple(
                            stringResource(R.string.tab_tools_catalog),
                            Icons.Filled.Apps to Icons.Outlined.Apps,
                            "tab_tools_catalog"
                        )
                    )

                    tabItems.forEachIndexed { index, (label, icons, tag) ->
                        val isSelected = selectedTab == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(index) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) icons.first else icons.second,
                                    contentDescription = label
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NexusNeonBlue,
                                selectedTextColor = NexusNeonBlue,
                                indicatorColor = NexusNeonBlue.copy(alpha = 0.16f),
                                unselectedIconColor = NexusTextSecondary,
                                unselectedTextColor = NexusTextSecondary
                            ),
                            modifier = Modifier.testTag(tag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = selectedTab,
                label = "nexus_tab_transition"
            ) { tab ->
                when (tab) {
                    0 -> WebsiteCreatorScreen(
                        weatherCityInput = weatherCityInput,
                        weatherUiState = weatherUiState,
                        onWeatherCityChange = viewModel::updateWeatherCityInput,
                        onWeatherRefreshCity = viewModel::refreshWeatherByCity,
                        onWeatherUseGps = ::requestGpsWeather,
                        onOpenAlarm = { viewModel.openAlarmSheet() },
                        onOpenCalculator = { viewModel.openCalculator() },
                        onOpenYouTube = { viewModel.openYouTubeSearch("") },
                        onOpenMapsGps = { openMapsGps(weatherCityInput) },
                        onOpenNexusLanding = viewModel::openNexusLanding,
                        corporateUrlInput = corporateUrlInput,
                        corporateAntivirusState = corporateAntivirusState,
                        onCorporateUrlChange = viewModel::updateCorporateUrlInput,
                        onCorporateVerifyClick = viewModel::verifyCorporateLink,
                        isGameMode = isGameMode,
                        gameIdeaInput = gameIdeaInput,
                        onSetGameMode = viewModel::setGameMode,
                        onGameIdeaChange = viewModel::updateGameIdeaInput,
                        onGenerateGameClick = viewModel::generateGame,
                        formInput = websiteForm,
                        uiState = websiteUiState,
                        siteHistory = siteHistory,
                        onFormChange = viewModel::updateWebsiteForm,
                        onFillExample = viewModel::fillSampleWebsiteForm,
                        onGenerateClick = viewModel::generateWebsite,
                        onLoadFromHistory = viewModel::loadSiteFromHistory,
                        onDeleteHistoryEntry = viewModel::deleteSiteFromHistory,
                        onDictateField = ::startDictation
                    )

                    1 -> AiAntivirusScreen(
                        urlInput = urlInput,
                        uiState = antivirusUiState,
                        onUrlChange = viewModel::updateUrlInput,
                        onAnalyzeClick = viewModel::analyzeUrl,
                        onDictateField = ::startDictation
                    )

                    else -> ToolsCatalogScreen(
                        tools = filteredTools,
                        totalCount = ToolsCatalog.allTools.size,
                        commercialChannel = commercialChannel,
                        commercialBusiness = commercialBusiness,
                        commercialTopic = commercialTopic,
                        commercialCopyUiState = commercialCopyUiState,
                        onSelectCommercialChannel = viewModel::selectCommercialChannel,
                        onCommercialBusinessChange = viewModel::updateCommercialBusiness,
                        onCommercialTopicChange = viewModel::updateCommercialTopic,
                        onGenerateCommercialCopy = viewModel::generateCommercialCopy,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        favoritesOnly = favoritesOnly,
                        favoriteIds = favoriteIds,
                        activeAssistantTool = activeAssistantTool,
                        assistantQuestion = assistantQuestion,
                        assistantUiState = assistantUiState,
                        onSearchQueryChange = viewModel::updateSearchQuery,
                        onSelectCategory = viewModel::selectCategory,
                        onToggleFavoritesOnly = viewModel::toggleFavoritesOnly,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onOpenAssistant = viewModel::openToolAssistant,
                        onCloseAssistant = viewModel::closeToolAssistant,
                        onAssistantQuestionChange = viewModel::updateAssistantQuestion,
                        onAskAssistant = viewModel::askToolAssistant,
                        onDictateField = ::startDictation
                    )
                }
            }
        }
    }

    if (isVipDialogOpen) {
        VipUnlockDialog(
            isVipUnlocked = isVipUnlocked,
            remainingFreeUsesToday = remainingFreeUsesToday,
            vipActivationError = vipActivationError,
            onActivateVipCode = { code ->
                val activated = viewModel.activateVipWithCode(code)
                if (activated) {
                    Toast.makeText(
                        context,
                        "Modo VIP Ativado! Acesso ilimitado liberado neste aparelho.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            onDictateCode = { cb -> startDictation("Diga o código VIP", cb) },
            onDismiss = viewModel::closeVipDialog
        )
    }

    if (isAlarmSheetOpen) {
        AlarmModalSheet(
            initialHour = alarmInitialHour,
            initialMinute = alarmInitialMinute,
            initialLabel = alarmInitialLabel,
            activeInAppAlarmLabel = activeInAppAlarmLabel,
            activeInAppAlarmTargetTimeText = activeInAppAlarmTimeText,
            onScheduleInAppAlarm = { h, m, lbl ->
                viewModel.scheduleInAppAlarm(h, m, lbl)
                Toast.makeText(
                    context,
                    "Alarme interno agendado para %02d:%02d".format(h, m),
                    Toast.LENGTH_LONG
                ).show()
            },
            onCancelInAppAlarm = viewModel::cancelInAppAlarm,
            onTestRingNow = { viewModel.triggerAlarmSoundAndVibration("Teste de Alarme NEXUS AI 100") },
            onDictateLabel = { cb -> startDictation("Diga o lembrete do alarme", cb) },
            onDismiss = viewModel::closeAlarmSheet
        )
    }

    if (ringingAlarmLabel != null) {
        AlarmRingingDialog(
            alarmLabel = ringingAlarmLabel.orEmpty(),
            onStopAlarm = viewModel::stopAlarmSoundAndVibration
        )
    }

    if (isCalculatorOpen) {
        CalculatorModalSheet(
            initialExpression = calcInitialExpression,
            initialResultText = calcInitialResult,
            isVoiceMuted = isVoiceMuted,
            onToggleMute = { voiceManager.toggleMute() },
            onSpeakText = { text -> voiceManager.speak(text) },
            onDismiss = viewModel::closeCalculator
        )
    }

    if (youtubeQueryOpen != null) {
        YouTubeSearchSheet(
            initialQuery = youtubeQueryOpen.orEmpty(),
            onDictateQuery = { cb -> startDictation("O que deseja buscar no YouTube?", cb) },
            onDismiss = viewModel::closeYouTubeSearch
        )
    }

    if (isNexusLandingOpen) {
        NexusLandingPageSheet(
            onDictateField = ::startDictation,
            onDismiss = viewModel::closeNexusLanding
        )
    }

    if (isVoiceSheetOpen) {
        UniversalVoiceAssistantSheet(
            isListening = isListeningVoice,
            isSpeaking = isSpeakingVoice,
            isVoiceMuted = isVoiceMuted,
            recognizedQuery = recognizedVoiceText,
            uiState = universalAssistantUiState,
            onStartListening = {
                startDictation("Fale sua pergunta ou comando...") { spoken ->
                    viewModel.routeVoiceCommand(
                        spokenText = spoken,
                        onSpeakResponse = { reply -> voiceManager.speak(reply) },
                        onOpenMapsGps = ::openMapsGps
                    )
                }
            },
            onStopListening = { voiceManager.stopListening() },
            onSubmitTextCommand = { cmd ->
                viewModel.routeVoiceCommand(
                    spokenText = cmd,
                    onSpeakResponse = { reply -> voiceManager.speak(reply) },
                    onOpenMapsGps = ::openMapsGps
                )
            },
            onToggleMute = { voiceManager.toggleMute() },
            onSpeakAgain = { text ->
                voiceManager.setMuted(false)
                voiceManager.speak(text)
            },
            onDismiss = {
                voiceManager.stopListening()
                viewModel.closeVoiceAssistantSheet()
            }
        )
    }
}
