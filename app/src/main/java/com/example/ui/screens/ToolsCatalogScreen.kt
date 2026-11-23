package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AssistantUiState
import com.example.CommercialCopyUiState
import com.example.data.AiTool
import com.example.data.CommercialChannel
import com.example.data.ToolCategory
import com.example.ui.components.MicDictationButton
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NexusBgDeep
import com.example.ui.theme.NexusBorder
import com.example.ui.theme.NexusEmerald
import com.example.ui.theme.NexusNeonBlue
import com.example.ui.theme.NexusSurface
import com.example.ui.theme.NexusSurfaceElevated
import com.example.ui.theme.NexusTextMuted
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsCatalogScreen(
    tools: List<AiTool>,
    totalCount: Int,
    commercialChannel: CommercialChannel,
    commercialBusiness: String,
    commercialTopic: String,
    commercialCopyUiState: CommercialCopyUiState,
    onSelectCommercialChannel: (CommercialChannel) -> Unit,
    onCommercialBusinessChange: (String) -> Unit,
    onCommercialTopicChange: (String) -> Unit,
    onGenerateCommercialCopy: () -> Unit,
    searchQuery: String,
    selectedCategory: ToolCategory,
    favoritesOnly: Boolean,
    favoriteIds: Set<String>,
    activeAssistantTool: AiTool?,
    assistantQuestion: String,
    assistantUiState: AssistantUiState,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (ToolCategory) -> Unit,
    onToggleFavoritesOnly: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenAssistant: (AiTool) -> Unit,
    onCloseAssistant: () -> Unit,
    onAssistantQuestionChange: (String) -> Unit,
    onAskAssistant: (String?) -> Unit,
    onDictateField: (label: String, onResult: (String) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
                .testTag("tools_catalog_list")
        ) {
            item(key = "commercial_copy_generator") {
                CommercialCopyGeneratorCard(
                    channel = commercialChannel,
                    business = commercialBusiness,
                    topic = commercialTopic,
                    uiState = commercialCopyUiState,
                    onSelectChannel = onSelectCommercialChannel,
                    onBusinessChange = onCommercialBusinessChange,
                    onTopicChange = onCommercialTopicChange,
                    onGenerateClick = onGenerateCommercialCopy,
                    onDictateField = onDictateField
                )
            }

            item(key = "catalog_search_and_filters") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Buscar entre 100 ferramentas IA...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null,
                                tint = NexusNeonBlue
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MicDictationButton(
                                    contentDescription = "Buscar ferramenta por voz",
                                    onClick = {
                                        onDictateField("Diga o nome ou categoria") { spoken ->
                                            onSearchQueryChange(spoken)
                                        }
                                    },
                                    tag = "mic_tools_search"
                                )
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchQueryChange("") }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Clear,
                                            contentDescription = "Limpar busca",
                                            tint = NexusTextSecondary
                                        )
                                    }
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NexusNeonBlue,
                            unfocusedBorderColor = NexusBorder,
                            focusedContainerColor = NexusSurface,
                            unfocusedContainerColor = NexusSurface,
                            focusedTextColor = NexusTextPrimary,
                            unfocusedTextColor = NexusTextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tools_search_input")
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(ToolCategory.entries) { category ->
                            val isSelected = selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectCategory(category) },
                                label = { Text(category.label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = NexusSurface,
                                    labelColor = NexusTextSecondary,
                                    selectedContainerColor = NexusNeonBlue.copy(alpha = 0.2f),
                                    selectedLabelColor = NexusNeonBlue
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = NexusBorder,
                                    selectedBorderColor = NexusNeonBlue
                                ),
                                modifier = Modifier.minimumInteractiveComponentSize()
                            )
                        }

                        item {
                            FilterChip(
                                selected = favoritesOnly,
                                onClick = onToggleFavoritesOnly,
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (favoritesOnly) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = null,
                                        tint = if (favoritesOnly) NexusEmerald else NexusTextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = { Text("Favoritas (${favoriteIds.size})") },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = NexusSurface,
                                    labelColor = NexusTextSecondary,
                                    selectedContainerColor = NexusEmerald.copy(alpha = 0.2f),
                                    selectedLabelColor = NexusEmerald
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = favoritesOnly,
                                    borderColor = NexusBorder,
                                    selectedBorderColor = NexusEmerald
                                ),
                                modifier = Modifier.minimumInteractiveComponentSize()
                            )
                        }
                    }

                    Text(
                        text = "Mostrando ${tools.size} de $totalCount ferramentas • ${favoriteIds.size} salvas",
                        style = MaterialTheme.typography.labelMedium,
                        color = NexusTextSecondary
                    )
                }
            }

            if (tools.isEmpty()) {
                item(key = "empty_tools_state") {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SmartToy,
                            contentDescription = null,
                            tint = NexusTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Nenhuma ferramenta encontrada.",
                            style = MaterialTheme.typography.titleMedium,
                            color = NexusTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tente outro termo ou limpe os filtros.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTextSecondary
                        )
                    }
                }
            } else {
                items(tools, key = { it.id }) { tool ->
                    val isFav = favoriteIds.contains(tool.id)
                    ToolCard(
                        tool = tool,
                        isFavorite = isFav,
                        onToggleFavorite = { onToggleFavorite(tool.id) },
                        onOpenAssistant = { onOpenAssistant(tool) },
                        onOpenWebsite = {
                            runCatching {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse(tool.officialUrl))
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    if (activeAssistantTool != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        BackHandler { onCloseAssistant() }
        ModalBottomSheet(
            onDismissRequest = onCloseAssistant,
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NexusNeonBlue.copy(alpha = 0.14f))
                            .border(1.dp, NexusNeonBlue.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = NexusNeonBlue
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Assistente IA • ${activeAssistantTool.name}",
                            style = MaterialTheme.typography.titleMedium,
                            color = NexusTextPrimary
                        )
                        Text(
                            text = "${activeAssistantTool.category.label} • ${activeAssistantTool.tag}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusEmerald
                        )
                    }
                }

                Text(
                    text = activeAssistantTool.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexusTextSecondary
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val quickPrompts = listOf(
                        "Como começar grátis?",
                        "3 prompts prontos",
                        "Uso para meu negócio"
                    )
                    items(quickPrompts) { q ->
                        AssistChip(
                            onClick = { onAskAssistant(q) },
                            label = { Text(q) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = NexusBgDeep,
                                labelColor = NexusNeonBlue
                            ),
                            border = BorderStroke(1.dp, NexusBorder)
                        )
                    }
                }

                OutlinedTextField(
                    value = assistantQuestion,
                    onValueChange = onAssistantQuestionChange,
                    label = { Text("Pergunte como usar esta ferramenta") },
                    placeholder = { Text("Ex.: Como criar um anúncio com ${activeAssistantTool.name}?") },
                    trailingIcon = {
                        MicDictationButton(
                            contentDescription = "Ditar pergunta",
                            onClick = {
                                onDictateField("Diga sua pergunta sobre ${activeAssistantTool.name}") { spoken ->
                                    onAssistantQuestionChange(spoken)
                                }
                            },
                            tag = "mic_tool_assistant"
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
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { onAskAssistant(null) },
                    enabled = assistantUiState !is AssistantUiState.Loading,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NexusNeonBlue,
                        contentColor = Color(0xFF04131E)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (assistantUiState is AssistantUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.5.dp,
                            color = Color(0xFF04131E)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Consultando IA...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gerar Guia & Prompt com IA", fontWeight = FontWeight.Bold)
                    }
                }

                if (assistantUiState is AssistantUiState.Error) {
                    ErrorActionCard(
                        message = assistantUiState.message
                    )
                }

                if (assistantUiState is AssistantUiState.Success) {
                    val res = assistantUiState.response
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NexusBgDeep),
                        border = BorderStroke(1.dp, NexusBorder),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = res.guidance,
                                style = MaterialTheme.typography.bodyMedium,
                                color = NexusTextPrimary
                            )
                            if (res.readyPrompt.isNotBlank()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(NexusSurface)
                                        .border(1.dp, NexusEmerald.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Prompt Pronto para Copiar",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = NexusEmerald
                                        )
                                        IconButton(
                                            onClick = {
                                                clipboard.setText(AnnotatedString(res.readyPrompt))
                                                Toast.makeText(
                                                    context,
                                                    "Prompt copiado!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.ContentCopy,
                                                contentDescription = "Copiar prompt",
                                                tint = NexusNeonBlue,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = res.readyPrompt,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 12.sp,
                                        color = NexusTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommercialCopyGeneratorCard(
    channel: CommercialChannel,
    business: String,
    topic: String,
    uiState: CommercialCopyUiState,
    onSelectChannel: (CommercialChannel) -> Unit,
    onBusinessChange: (String) -> Unit,
    onTopicChange: (String) -> Unit,
    onGenerateClick: () -> Unit,
    onDictateField: (label: String, onResult: (String) -> Unit) -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    Card(
        colors = CardDefaults.cardColors(containerColor = NexusSurface),
        border = BorderStroke(1.dp, NexusNeonBlue.copy(alpha = 0.65f)),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("commercial_copy_card")
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NexusEmerald.copy(alpha = 0.16f))
                        .border(1.dp, NexusEmerald.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Campaign,
                        contentDescription = null,
                        tint = NexusEmerald
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Textos e Propostas Comerciais IA",
                        style = MaterialTheme.typography.titleMedium,
                        color = NexusTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Gere mensagens prontas para WhatsApp, E-mail e Anúncios gratuitamente com IA.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTextSecondary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CommercialChannel.entries.forEach { ch ->
                    val selected = ch == channel
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectChannel(ch) },
                        label = { Text(ch.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = NexusBgDeep,
                            labelColor = NexusTextSecondary,
                            selectedContainerColor = NexusEmerald.copy(alpha = 0.2f),
                            selectedLabelColor = NexusEmerald
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = NexusBorder,
                            selectedBorderColor = NexusEmerald
                        ),
                        modifier = Modifier.minimumInteractiveComponentSize()
                    )
                }
            }

            OutlinedTextField(
                value = business,
                onValueChange = onBusinessChange,
                label = { Text("Empresa ou Ramo (opcional)") },
                placeholder = { Text("Ex.: Clínica Odontológica Sorriso") },
                trailingIcon = {
                    MicDictationButton(
                        contentDescription = "Ditar empresa ou ramo",
                        onClick = {
                            onDictateField("Diga o nome da empresa ou ramo") { spoken ->
                                onBusinessChange(spoken)
                            }
                        },
                        tag = "mic_commercial_business"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NexusNeonBlue,
                    unfocusedBorderColor = NexusBorder,
                    focusedContainerColor = NexusBgDeep,
                    unfocusedContainerColor = NexusBgDeep,
                    focusedTextColor = NexusTextPrimary,
                    unfocusedTextColor = NexusTextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = topic,
                onValueChange = onTopicChange,
                label = { Text("Objetivo do Texto ou Proposta") },
                placeholder = { Text("Ex.: Reativar clientes antigos pelo WhatsApp") },
                trailingIcon = {
                    MicDictationButton(
                        contentDescription = "Ditar objetivo comercial",
                        onClick = {
                            onDictateField("Diga o objetivo do texto") { spoken ->
                                onTopicChange(spoken)
                            }
                        },
                        tag = "mic_commercial_topic"
                    )
                },
                minLines = 2,
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
                    .testTag("commercial_topic_input")
            )

            Button(
                onClick = onGenerateClick,
                enabled = uiState !is CommercialCopyUiState.Loading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NexusEmerald,
                    contentColor = Color(0xFF031F16),
                    disabledContainerColor = NexusEmerald.copy(alpha = 0.45f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("generate_commercial_copy_button")
            ) {
                if (uiState is CommercialCopyUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.5.dp,
                        color = Color(0xFF031F16)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gerando Texto com IA...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gerar Proposta / Texto IA", fontWeight = FontWeight.Bold)
                }
            }

            if (uiState is CommercialCopyUiState.Error) {
                ErrorActionCard(
                    message = uiState.message
                )
            }

            if (uiState is CommercialCopyUiState.Success) {
                val copy = uiState.copy
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(NexusBgDeep)
                        .border(1.dp, NexusEmerald.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = copy.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = NexusNeonBlue,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = copy.body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexusTextPrimary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                clipboard.setText(AnnotatedString("${copy.title}\n\n${copy.body}"))
                                Toast.makeText(context, "Texto copiado!", Toast.LENGTH_SHORT).show()
                            },
                            border = BorderStroke(1.dp, NexusNeonBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = null,
                                tint = NexusNeonBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copiar Texto", color = NexusNeonBlue)
                        }
                        OutlinedButton(
                            onClick = {
                                val waUri = Uri.parse("https://wa.me/?text=${Uri.encode(copy.body)}")
                                runCatching {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, waUri))
                                }.onFailure {
                                    val fallback = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, copy.body)
                                    }
                                    context.startActivity(Intent.createChooser(fallback, "Enviar Texto"))
                                }
                            },
                            border = BorderStroke(1.dp, NexusEmerald),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = null,
                                tint = NexusEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WhatsApp", color = NexusEmerald)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolCard(
    tool: AiTool,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onOpenAssistant: () -> Unit,
    onOpenWebsite: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NexusSurface),
        border = BorderStroke(
            width = 1.dp,
            color = if (isFavorite) NexusEmerald.copy(alpha = 0.5f) else NexusBorder
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "#%03d".format(tool.rank),
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NexusNeonBlue,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NexusNeonBlue.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                        Text(
                            text = tool.category.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = NexusEmerald,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NexusEmerald.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                        Text(
                            text = tool.tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = NexusTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = tool.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = NexusTextPrimary
                    )
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("favorite_tool_${tool.id}")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remover dos favoritos" else "Favoritar ferramenta",
                        tint = if (isFavorite) NexusEmerald else NexusTextSecondary
                    )
                }
            }

            Text(
                text = tool.description,
                style = MaterialTheme.typography.bodySmall,
                color = NexusTextSecondary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenAssistant,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NexusSurfaceElevated,
                        contentColor = NexusNeonBlue
                    ),
                    border = BorderStroke(1.dp, NexusNeonBlue.copy(alpha = 0.4f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("tool_assistant_button_${tool.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Assistente IA",
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1
                    )
                }

                OutlinedButton(
                    onClick = onOpenWebsite,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, NexusBorder),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.OpenInNew,
                        contentDescription = "Abrir ${tool.name}",
                        tint = NexusTextPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Abrir",
                        style = MaterialTheme.typography.labelLarge,
                        color = NexusTextPrimary
                    )
                }
            }
        }
    }
}
