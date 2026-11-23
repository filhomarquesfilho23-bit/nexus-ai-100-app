package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Web
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.NexusIaLandingTemplate
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NexusAmber
import com.example.ui.theme.NexusBgDeep
import com.example.ui.theme.NexusBorder
import com.example.ui.theme.NexusDangerRed
import com.example.ui.theme.NexusEmerald
import com.example.ui.theme.NexusNeonBlue
import com.example.ui.theme.NexusSurface
import com.example.ui.theme.NexusSurfaceElevated
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NexusLandingPageSheet(
    onDictateField: (label: String, onResult: (String) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    val htmlCode = remember { NexusIaLandingTemplate.buildHtml() }
    var activeTab by remember { mutableIntStateOf(0) } // 0 = Interativo, 1 = HTML Preview, 2 = Código

    val createHtmlLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/html")
    ) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(htmlCode.toByteArray(Charsets.UTF_8))
                }
            }.onSuccess {
                Toast.makeText(context, "Landing Page Nexus IA salva em HTML!", Toast.LENGTH_LONG).show()
            }.onFailure { err ->
                Toast.makeText(context, "Erro ao salvar: ${err.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

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
                // Header Toolbar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NexusSurface)
                        .statusBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(listOf(NexusNeonBlue, NexusEmerald))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFF04131E),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Nexus IA",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = NexusTextPrimary
                                )
                                Text(
                                    text = "Landing Page de Alta Conversão",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexusNeonBlue
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    clipboard.setText(AnnotatedString(htmlCode))
                                    Toast.makeText(context, "HTML da Nexus IA copiado!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ContentCopy,
                                    contentDescription = "Copiar HTML Nexus IA",
                                    tint = NexusNeonBlue
                                )
                            }
                            IconButton(
                                onClick = { createHtmlLauncher.launch("landing-page-nexus-ia.html") },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Download,
                                    contentDescription = "Baixar HTML Nexus IA",
                                    tint = NexusEmerald
                                )
                            }
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Fechar Landing Page",
                                    tint = NexusTextPrimary
                                )
                            }
                        }
                    }

                    // 3-way view selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NexusBgDeep)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        LandingModeButton(
                            selected = activeTab == 0,
                            label = "Interativa",
                            icon = Icons.Filled.AutoAwesome,
                            onClick = { activeTab = 0 },
                            modifier = Modifier.weight(1f)
                        )
                        LandingModeButton(
                            selected = activeTab == 1,
                            label = "Prévia Web",
                            icon = Icons.Outlined.Web,
                            onClick = { activeTab = 1 },
                            modifier = Modifier.weight(1f)
                        )
                        LandingModeButton(
                            selected = activeTab == 2,
                            label = "Código HTML",
                            icon = Icons.Outlined.Code,
                            onClick = { activeTab = 2 },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                when (activeTab) {
                    0 -> {
                        val pageScroll = rememberScrollState()
                        InteractiveNexusIaLandingContent(
                            scrollState = pageScroll,
                            onScrollToForm = {
                                scope.launch { pageScroll.animateScrollTo(pageScroll.maxValue) }
                            },
                            onDictateField = onDictateField
                        )
                    }

                    1 -> {
                        LandingHtmlWebView(
                            htmlCode = htmlCode,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(NexusSurface)
                                    .border(1.dp, NexusBorder, RoundedCornerShape(14.dp))
                                    .padding(12.dp)
                                    .horizontalScroll(rememberScrollState())
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = htmlCode,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp,
                                    color = NexusNeonBlue.copy(alpha = 0.92f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        clipboard.setText(AnnotatedString(htmlCode))
                                        Toast.makeText(context, "Código HTML copiado!", Toast.LENGTH_SHORT).show()
                                    },
                                    border = BorderStroke(1.dp, NexusNeonBlue),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                ) {
                                    Icon(Icons.Outlined.ContentCopy, contentDescription = null, tint = NexusNeonBlue, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Copiar HTML", color = NexusNeonBlue)
                                }
                                Button(
                                    onClick = { createHtmlLauncher.launch("landing-page-nexus-ia.html") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NexusEmerald,
                                        contentColor = Color(0xFF031F16)
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                ) {
                                    Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Baixar HTML", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InteractiveNexusIaLandingContent(
    scrollState: androidx.compose.foundation.ScrollState,
    onScrollToForm: () -> Unit,
    onDictateField: (label: String, onResult: (String) -> Unit) -> Unit
) {
    val context = LocalContext.current

    var clientName by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    val serviceOptions = remember {
        listOf(
            "Landing Page" to "Landing Page (Mais rápido)",
            "Site Institucional" to "Site Institucional",
            "Catálogo Virtual" to "Catálogo Virtual / E-commerce",
            "Outro" to "Outro serviço"
        )
    }
    var selectedSiteOption by remember { mutableStateOf(serviceOptions.first()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var businessDesc by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }

    val ctaInteraction = remember { MutableInteractionSource() }
    val ctaPressed by ctaInteraction.collectIsPressedAsState()
    val ctaScale by animateFloatAsState(
        targetValue = if (ctaPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = 600f),
        label = "cta_press"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Hero Dark Cyber
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                NexusNeonBlue.copy(alpha = 0.14f),
                                NexusSurface,
                                NexusEmerald.copy(alpha = 0.08f)
                            )
                        )
                    )
                    .border(1.dp, NexusNeonBlue.copy(alpha = 0.45f), RoundedCornerShape(22.dp))
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(NexusNeonBlue.copy(alpha = 0.15f))
                        .border(1.dp, NexusNeonBlue.copy(alpha = 0.5f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = NexusNeonBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sites Profissionais",
                        style = MaterialTheme.typography.labelLarge,
                        color = NexusNeonBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Tenha um Site que Vende Todos os Dias",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 28.sp,
                        lineHeight = 34.sp
                    ),
                    fontWeight = FontWeight.ExtraBold,
                    color = NexusTextPrimary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Criamos sites modernos, ultra-rápidos e sob medida para alavancar as vendas da sua empresa.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexusTextSecondary,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = onScrollToForm,
                    interactionSource = ctaInteraction,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NexusEmerald,
                        contentColor = Color(0xFF031F16)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .scale(ctaScale)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Solicitar Orçamento Grátis",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 2. Seção de Serviços (3 cards)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Nossos Serviços",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NexusTextPrimary
                )

                LandingServiceCard(
                    title = "Landing Pages de Vendas",
                    description = "Páginas com foco total em converter visitantes em clientes. Ideais para quem faz anúncios.",
                    icon = Icons.Filled.RocketLaunch,
                    accent = NexusNeonBlue
                )
                LandingServiceCard(
                    title = "Catálogos Digitais",
                    description = "Exiba e venda seus produtos sem complicações, com botões integrados ao WhatsApp.",
                    icon = Icons.Filled.Storefront,
                    accent = NexusEmerald
                )
                LandingServiceCard(
                    title = "Sites Institucionais",
                    description = "Mais credibilidade para seu negócio com uma presença online profissional, robusta e confiável.",
                    icon = Icons.Filled.Apartment,
                    accent = NexusNeonBlue
                )
            }

            // 3. Seção Como Funciona (3 passos + depoimento Pedro Rocha)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Como Funciona",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NexusTextPrimary
                )

                LandingStepCard(
                    stepBadge = "01. Envie a Ideia",
                    description = "Você informa os detalhes da sua empresa e escolhe o modelo ideal em menos de 2 minutos."
                )
                LandingStepCard(
                    stepBadge = "02. Criação",
                    description = "Nossa IA e design dark cyber estruturam páginas rápidas, responsivas e prontas para celular."
                )
                LandingStepCard(
                    stepBadge = "03. Resultado",
                    description = "Seu site entra no ar com botões diretos para receber novos clientes no WhatsApp."
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = NexusSurface),
                    border = BorderStroke(1.dp, NexusEmerald.copy(alpha = 0.55f)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        NexusNeonBlue.copy(alpha = 0.08f),
                                        NexusEmerald.copy(alpha = 0.12f)
                                    )
                                )
                            )
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(5) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = NexusAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = "\"Tivemos um aumento enorme de contatos de clientes no WhatsApp no primeiro mês que o site foi publicado\"",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = NexusTextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(NexusEmerald.copy(alpha = 0.2f))
                                    .border(1.dp, NexusEmerald, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "PR",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = NexusEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Pedro Rocha",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = NexusTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Empresário local",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // 4. Formulário Interativo "Solicitar Orçamento Grátis"
            Card(
                colors = CardDefaults.cardColors(containerColor = NexusSurface),
                border = BorderStroke(1.5.dp, NexusNeonBlue.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("nexus_landing_form_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Solicitar Orçamento Grátis",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NexusTextPrimary
                    )
                    Text(
                        text = "Preencha os dados e envie sua solicitação direto para o WhatsApp da Nexus IA.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTextSecondary
                    )

                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("Seu Nome") },
                        placeholder = { Text("Ex.: João Silva") },
                        singleLine = true,
                        trailingIcon = {
                            MicDictationButton(
                                contentDescription = "Ditar seu nome",
                                onClick = { onDictateField("Diga seu nome") { clientName = it } },
                                tag = "mic_landing_name"
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
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
                            .testTag("landing_name_input")
                    )

                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("Nome da Empresa") },
                        placeholder = { Text("Ex.: Clínica Vida Plena") },
                        singleLine = true,
                        trailingIcon = {
                            MicDictationButton(
                                contentDescription = "Ditar nome da empresa",
                                onClick = { onDictateField("Diga o nome da empresa") { companyName = it } },
                                tag = "mic_landing_company"
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
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
                            .testTag("landing_company_input")
                    )

                    ExposedDropdownMenuBox(
                        expanded = isDropdownExpanded,
                        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedSiteOption.second,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de Site Desejado") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
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
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .testTag("landing_type_select")
                        )

                        ExposedDropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier.background(NexusSurfaceElevated)
                        ) {
                            serviceOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.second, color = NexusTextPrimary) },
                                    onClick = {
                                        selectedSiteOption = option
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = businessDesc,
                        onValueChange = { businessDesc = it },
                        label = { Text("Fale brevemente do seu negócio") },
                        placeholder = { Text("Ex: Preciso de uma página para vender meus serviços de...") },
                        minLines = 3,
                        trailingIcon = {
                            MicDictationButton(
                                contentDescription = "Ditar descrição do negócio",
                                onClick = { onDictateField("Descreva seu negócio") { businessDesc = it } },
                                tag = "mic_landing_desc"
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
                            .testTag("landing_desc_input")
                    )

                    if (!formError.isNullOrBlank()) {
                        Text(
                            text = formError.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusDangerRed
                        )
                    }

                    // 5. Botão verde "Enviar para o WhatsApp"
                    val sendInteraction = remember { MutableInteractionSource() }
                    val sendPressed by sendInteraction.collectIsPressedAsState()
                    val sendScale by animateFloatAsState(
                        targetValue = if (sendPressed) 0.96f else 1f,
                        animationSpec = spring(stiffness = 600f),
                        label = "whatsapp_send_press"
                    )

                    Button(
                        onClick = {
                            val n = clientName.trim()
                            val e = companyName.trim()
                            val d = businessDesc.trim()
                            if (n.isEmpty() || e.isEmpty()) {
                                formError = "Por favor, preencha pelo menos o seu nome e o da sua empresa!"
                                return@Button
                            }
                            formError = null
                            val message = "Olá! Vim através do site e gostaria de solicitar um orçamento para:\n\n" +
                                "👤 *Nome:* $n\n" +
                                "🏢 *Empresa:* $e\n" +
                                "🌐 *Tipo de Site:* ${selectedSiteOption.first}\n" +
                                "📝 *Sobre o negócio:* $d"
                            val waUri = Uri.parse(
                                "https://api.whatsapp.com/send?phone=${NexusIaLandingTemplate.DEFAULT_WHATSAPP_NUMBER}&text=${Uri.encode(message)}"
                            )
                            runCatching {
                                context.startActivity(Intent(Intent.ACTION_VIEW, waUri))
                            }.onFailure {
                                val fallback = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, message)
                                }
                                context.startActivity(Intent.createChooser(fallback, "Enviar orçamento no WhatsApp"))
                            }
                        },
                        interactionSource = sendInteraction,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NexusEmerald,
                            contentColor = Color(0xFF031F16)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .scale(sendScale)
                            .testTag("landing_whatsapp_submit")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Enviar para o WhatsApp",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LandingServiceCard(
    title: String,
    description: String,
    icon: ImageVector,
    accent: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NexusSurface),
        border = BorderStroke(1.dp, NexusBorder),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.14f))
                    .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accent,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NexusTextPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = NexusTextSecondary
                )
            }
        }
    }
}

@Composable
private fun LandingStepCard(
    stepBadge: String,
    description: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NexusSurface),
        border = BorderStroke(1.dp, NexusBorder),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(NexusNeonBlue)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stepBadge,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF04131E)
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = NexusTextSecondary
            )
        }
    }
}

@Composable
private fun LandingModeButton(
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
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
        modifier = modifier.height(44.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium, maxLines = 1)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun LandingHtmlWebView(
    htmlCode: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                webChromeClient = WebChromeClient()
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url?.toString() ?: return false
                        if (url.startsWith("https://api.whatsapp.com/") ||
                            url.startsWith("https://wa.me/") ||
                            url.startsWith("whatsapp://") ||
                            url.startsWith("http://") ||
                            url.startsWith("https://")
                        ) {
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
