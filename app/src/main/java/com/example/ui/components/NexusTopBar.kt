package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.LocalPreferencesStore
import com.example.ui.theme.NexusBgDeep
import com.example.ui.theme.NexusBorder
import com.example.ui.theme.NexusDangerRed
import com.example.ui.theme.NexusEmerald
import com.example.ui.theme.NexusNeonBlue
import com.example.ui.theme.NexusSurface
import com.example.ui.theme.NexusTextPrimary

@Composable
fun NexusTopBar(
    hasCustomKeySaved: Boolean,
    isListeningVoice: Boolean,
    isVipUnlocked: Boolean,
    remainingFreeUsesToday: Int,
    onOpenVipDialog: () -> Unit,
    onOpenVoiceAssistant: () -> Unit,
    onOpenApiSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NexusSurface, NexusBgDeep)
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, NexusNeonBlue.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nexus_icon_fg_1794786392551),
                        contentDescription = stringResource(R.string.brand_header_title),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(38.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.brand_header_title),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = NexusTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(NexusEmerald.copy(alpha = 0.16f))
                                .border(1.dp, NexusEmerald.copy(alpha = 0.45f), RoundedCornerShape(999.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.brand_free_badge),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = NexusEmerald,
                                maxLines = 1
                            )
                        }
                    }
                    Text(
                        text = "Sites • Antivírus • 100 IAs • Utilitários",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusNeonBlue.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(
                onClick = onOpenApiSettings,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(NexusSurface)
                    .border(
                        width = 1.dp,
                        color = if (hasCustomKeySaved) NexusEmerald.copy(alpha = 0.6f) else NexusBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .testTag("api_key_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Key,
                    contentDescription = stringResource(R.string.api_settings_cd),
                    tint = if (hasCustomKeySaved) NexusEmerald else NexusNeonBlue
                )
            }
        }

        // Indicador discreto de créditos gratuitos diários / Modo VIP
        val statusAccent = when {
            isVipUnlocked -> NexusEmerald
            remainingFreeUsesToday > 0 -> NexusNeonBlue
            else -> NexusDangerRed
        }
        val statusLabel = if (isVipUnlocked) {
            "Modo VIP Ativo • Acesso Ilimitado às IAs"
        } else {
            "$remainingFreeUsesToday/${LocalPreferencesStore.MAX_FREE_DAILY_USES} testes gratuitos restantes hoje • Desbloquear VIP"
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(statusAccent.copy(alpha = 0.10f))
                .border(1.dp, statusAccent.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                .clickable(onClick = onOpenVipDialog)
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("daily_credits_vip_indicator"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isVipUnlocked) Icons.Filled.WorkspacePremium else Icons.Filled.Bolt,
                contentDescription = null,
                tint = statusAccent,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = statusLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = statusAccent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Assistente Universal por Voz fixo no topo da tela em destaque azul neon (#00d2ff)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            NexusNeonBlue,
                            Color(0xFF0EA5E9)
                        )
                    )
                )
                .clickable(onClick = onOpenVoiceAssistant)
                .padding(horizontal = 14.dp)
                .testTag("universal_voice_assistant_button"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isListeningVoice) Icons.Filled.GraphicEq else Icons.Filled.Mic,
                contentDescription = "Assistente Universal por Voz",
                tint = Color(0xFF04131E),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isListeningVoice) {
                    "Ouvindo sua voz em Português..."
                } else {
                    "Assistente Universal por Voz • Toque e Fale"
                },
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF04131E),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
