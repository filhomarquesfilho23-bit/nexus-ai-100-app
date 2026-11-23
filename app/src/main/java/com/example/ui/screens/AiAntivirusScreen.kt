package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.WarningAmber
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.AntivirusUiState
import com.example.R
import com.example.ui.components.MicDictationButton
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

@Composable
fun AiAntivirusScreen(
    urlInput: String,
    uiState: AntivirusUiState,
    onUrlChange: (String) -> Unit,
    onAnalyzeClick: () -> Unit,
    onDictateField: (label: String, onResult: (String) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current
    val scrollState = rememberScrollState()

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
            Card(
                colors = CardDefaults.cardColors(containerColor = NexusSurface),
                border = BorderStroke(1.dp, NexusBorder),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    NexusEmerald.copy(alpha = 0.14f),
                                    NexusNeonBlue.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(NexusEmerald.copy(alpha = 0.16f))
                            .border(1.dp, NexusEmerald.copy(alpha = 0.45f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = null,
                            tint = NexusEmerald,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "AI Antivírus Anti-Golpes",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NexusTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Cole qualquer link suspeito, Pix Copia e Cola ou boleto para a IA detectar phishing e golpes gratuitamente.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTextSecondary
                        )
                    }
                }
            }

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
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = onUrlChange,
                        label = { Text("URL, Pix Copia e Cola ou Boleto") },
                        placeholder = { Text("https://exemplo.com/verificar") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Link,
                                contentDescription = null,
                                tint = NexusNeonBlue
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MicDictationButton(
                                    contentDescription = "Ditar link ou código",
                                    onClick = {
                                        onDictateField("Dite o endereço ou código") { spoken ->
                                            onUrlChange(spoken)
                                        }
                                    },
                                    tag = "mic_antivirus_url"
                                )
                                if (urlInput.isNotEmpty()) {
                                    IconButton(onClick = { onUrlChange("") }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Clear,
                                            contentDescription = "Limpar link",
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
                                        contentDescription = "Colar link",
                                        tint = NexusNeonBlue
                                    )
                                }
                            }
                        },
                        singleLine = true,
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
                            .testTag("url_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = { onUrlChange("https://www.gov.br/pt-br") },
                            label = { Text("Testar Seguro") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = NexusSurfaceElevated,
                                labelColor = NexusEmerald
                            ),
                            border = BorderStroke(1.dp, NexusEmerald.copy(alpha = 0.4f))
                        )
                        AssistChip(
                            onClick = { onUrlChange("http://pix-resgate-urgente.xyz/login-banco") },
                            label = { Text("Testar Golpe") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = NexusSurfaceElevated,
                                labelColor = NexusDangerRed
                            ),
                            border = BorderStroke(1.dp, NexusDangerRed.copy(alpha = 0.4f))
                        )
                    }

                    Button(
                        onClick = onAnalyzeClick,
                        enabled = uiState !is AntivirusUiState.Loading,
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
                            .testTag("analyze_link_button")
                    ) {
                        if (uiState is AntivirusUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp,
                                color = Color(0xFF04131E)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Analisando com IA...",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Security,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stringResource(R.string.analyze_link_cta),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = uiState is AntivirusUiState.Error) {
                val err = uiState as? AntivirusUiState.Error
                ErrorActionCard(
                    message = err?.message.orEmpty()
                )
            }

            if (uiState is AntivirusUiState.Success) {
                val analysis = uiState.result
                val statusColor = if (analysis.isSafe) NexusEmerald else NexusDangerRed
                val statusIcon = if (analysis.isSafe) Icons.Filled.VerifiedUser else Icons.Filled.GppBad

                Card(
                    colors = CardDefaults.cardColors(containerColor = NexusSurface),
                    border = BorderStroke(1.5.dp, statusColor.copy(alpha = 0.65f)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("antivirus_result_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(statusColor.copy(alpha = 0.15f))
                                .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = statusIcon,
                                contentDescription = analysis.badgeTitle,
                                tint = statusColor,
                                modifier = Modifier.size(30.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = analysis.badgeTitle,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = analysis.normalizedUrl,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    color = NexusTextSecondary
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Índice de Risco Estimado",
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
                                trackColor = NexusBgDeep,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(999.dp))
                            )
                        }

                        Text(
                            text = "Análise Detalhada",
                            style = MaterialTheme.typography.titleSmall,
                            color = NexusTextPrimary
                        )
                        Text(
                            text = analysis.detailedExplanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexusTextSecondary
                        )

                        if (analysis.indicators.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(NexusBgDeep)
                                    .border(1.dp, NexusBorder, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Sinais Verificados:",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = NexusNeonBlue
                                )
                                analysis.indicators.forEach { indicator ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(
                                            imageVector = if (analysis.isSafe) Icons.Outlined.CheckCircle else Icons.Outlined.WarningAmber,
                                            contentDescription = null,
                                            tint = statusColor,
                                            modifier = Modifier
                                                .padding(top = 2.dp)
                                                .size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = indicator,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = NexusTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
