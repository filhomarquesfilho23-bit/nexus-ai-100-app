package com.example.ui.components

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.ui.theme.NexusBorder
import com.example.ui.theme.NexusNeonBlue
import com.example.ui.theme.NexusSurface
import com.example.ui.theme.NexusTextMuted

/**
 * Contêiner dedicado para banner Adsterra Mobile 320x50 no TOPO (abaixo do cabeçalho).
 * Posicionado em fluxo próprio sem sobrepor botões clicáveis.
 */
@Composable
fun AdsterraTopBanner(
    modifier: Modifier = Modifier,
    adsterraHtmlSnippet: String = ""
) {
    // <!-- ESPAÇO BANNER ADSTERRA TOPO -->
    AdsterraBannerContainer(
        slotTag = "adsterra_top_banner",
        adsterraHtmlSnippet = adsterraHtmlSnippet,
        modifier = modifier
    )
}

/**
 * Contêiner dedicado para banner Adsterra Mobile 320x50 no RODAPÉ (acima da barra inferior).
 * Posicionado em fluxo próprio sem sobrepor botões clicáveis.
 */
@Composable
fun AdsterraBottomBanner(
    modifier: Modifier = Modifier,
    adsterraHtmlSnippet: String = ""
) {
    // <!-- ESPAÇO BANNER ADSTERRA RODAPE -->
    AdsterraBannerContainer(
        slotTag = "adsterra_bottom_banner",
        adsterraHtmlSnippet = adsterraHtmlSnippet,
        modifier = modifier
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun AdsterraBannerContainer(
    slotTag: String,
    adsterraHtmlSnippet: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(NexusSurface.copy(alpha = 0.85f))
                .border(1.dp, NexusBorder, RoundedCornerShape(10.dp))
                .testTag(slotTag),
            contentAlignment = Alignment.Center
        ) {
            if (adsterraHtmlSnippet.isNotBlank()) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        WebView(context).apply {
                            setBackgroundColor(android.graphics.Color.TRANSPARENT)
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            loadDataWithBaseURL(
                                "https://adsterra.com",
                                adsterraHtmlSnippet,
                                "text/html",
                                "UTF-8",
                                null
                            )
                        }
                    }
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Campaign,
                        contentDescription = null,
                        tint = NexusNeonBlue.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.ad_banner_label),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.4.sp
                        ),
                        color = NexusTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
