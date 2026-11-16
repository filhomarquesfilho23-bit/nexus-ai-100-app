package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.LocalPreferencesStore
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NexusBgDeep
import com.example.ui.theme.NexusBorder
import com.example.ui.theme.NexusDangerRed
import com.example.ui.theme.NexusEmerald
import com.example.ui.theme.NexusNeonBlue
import com.example.ui.theme.NexusSurface
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary

@Composable
fun VipUnlockDialog(
    isVipUnlocked: Boolean,
    remainingFreeUsesToday: Int,
    vipActivationError: String?,
    onActivateVipCode: (String) -> Unit,
    onDictateCode: (onResult: (String) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var codeInput by remember { mutableStateOf("") }
    val limitReached = !isVipUnlocked && remainingFreeUsesToday <= 0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = NexusSurface),
            border = BorderStroke(
                width = 1.5.dp,
                color = if (isVipUnlocked) NexusEmerald else NexusNeonBlue.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("vip_unlock_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                NexusNeonBlue.copy(alpha = 0.14f),
                                NexusEmerald.copy(alpha = 0.06f),
                                Color.Transparent
                            )
                        )
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isVipUnlocked) NexusEmerald.copy(alpha = 0.18f)
                                    else NexusNeonBlue.copy(alpha = 0.16f)
                                )
                                .border(
                                    1.dp,
                                    if (isVipUnlocked) NexusEmerald.copy(alpha = 0.55f)
                                    else NexusNeonBlue.copy(alpha = 0.5f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isVipUnlocked) Icons.Filled.Verified else Icons.Filled.WorkspacePremium,
                                contentDescription = null,
                                tint = if (isVipUnlocked) NexusEmerald else NexusNeonBlue,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = when {
                                    isVipUnlocked -> "Modo VIP Ilimitado Ativo"
                                    limitReached -> "Limite Diário Atingido — Modo VIP"
                                    else -> "Modo VIP — Acesso Ilimitado"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = NexusTextPrimary
                            )
                            Text(
                                text = when {
                                    isVipUnlocked -> "Todas as IAs avançadas liberadas sem limites."
                                    limitReached -> "Você utilizou os ${LocalPreferencesStore.MAX_FREE_DAILY_USES} testes gratuitos de hoje."
                                    else -> "$remainingFreeUsesToday/${LocalPreferencesStore.MAX_FREE_DAILY_USES} testes gratuitos restantes hoje."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isVipUnlocked) NexusEmerald else NexusNeonBlue
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Fechar janela VIP",
                            tint = NexusTextSecondary
                        )
                    }
                }

                if (!isVipUnlocked) {
                    // Info Card explaining gated vs always-free features
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(NexusBgDeep)
                            .border(1.dp, NexusBorder, RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Bolt,
                                contentDescription = null,
                                tint = NexusNeonBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "O que o Modo VIP desbloqueia sem limites:",
                                style = MaterialTheme.typography.labelLarge,
                                color = NexusTextPrimary
                            )
                        }
                        Text(
                            text = "• AI Antivírus Corporativo (Links, Pix e Boletos)\n" +
                                "• Criador de Sites IA e Criador de Jogos IA\n" +
                                "• Assistente Universal por Voz com Gemini",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTextSecondary
                        )
                        Text(
                            text = "Livre e ilimitado sempre: Previsão do Tempo, Despertador, Calculadora Falante, GPS, YouTube e Catálogo das 100 Ferramentas IA.",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexusEmerald
                        )
                    }

                    // Opção A: Botão Verde Mercado Pago
                    Button(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(LocalPreferencesStore.MERCADO_PAGO_VIP_URL)
                            )
                            runCatching {
                                context.startActivity(intent)
                            }.onFailure {
                                Toast.makeText(
                                    context,
                                    "Não foi possível abrir o navegador para o Mercado Pago.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NexusEmerald,
                            contentColor = Color(0xFF031F16)
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("vip_mercado_pago_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LockOpen,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Desbloquear Acesso Ilimitado (Mercado Pago)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Outlined.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    HorizontalDivider(color = NexusBorder)

                    // Opção B: Já tem o Código VIP?
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Já tem o Código VIP?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NexusNeonBlue
                        )

                        OutlinedTextField(
                            value = codeInput,
                            onValueChange = { codeInput = it },
                            label = { Text("Digite o código de ativação") },
                            placeholder = { Text("Ex.: NEXUS2026") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.VpnKey,
                                    contentDescription = null,
                                    tint = NexusNeonBlue
                                )
                            },
                            trailingIcon = {
                                MicDictationButton(
                                    contentDescription = "Ditar código VIP",
                                    onClick = {
                                        onDictateCode { spoken ->
                                            codeInput = spoken.replace(" ", "").uppercase()
                                        }
                                    },
                                    tag = "mic_vip_code"
                                )
                            },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                letterSpacing = 1.sp
                            ),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Characters,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { onActivateVipCode(codeInput) }
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
                                .testTag("vip_code_input")
                        )

                        if (!vipActivationError.isNullOrBlank()) {
                            Text(
                                text = vipActivationError,
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusDangerRed,
                                modifier = Modifier.testTag("vip_error_text")
                            )
                        }

                        Button(
                            onClick = { onActivateVipCode(codeInput) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NexusNeonBlue,
                                contentColor = Color(0xFF04131E)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("activate_vip_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.WorkspacePremium,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ativar VIP",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(NexusEmerald.copy(alpha = 0.14f))
                            .border(1.dp, NexusEmerald.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Licença VIP ativa neste aparelho!",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NexusEmerald
                        )
                        Text(
                            text = "Você possui acesso ilimitado ao AI Antivírus Corporativo, Criador de Sites IA, Criador de Jogos IA e Assistente Universal por Voz.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTextPrimary
                        )
                    }
                }
            }
        }
    }
}
