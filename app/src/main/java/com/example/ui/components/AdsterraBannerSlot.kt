package com.example.ui.components

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebSettings
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

private const val DEFAULT_ADSTERRA_320X50_HTML = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<style>
  html, body { margin: 0; padding: 0; background: transparent; width: 100%; height: 100%; overflow: hidden; display: flex; justify-content: center; align-items: center; }
</style>
</head>
<body>
<script type="text/javascript">
  atOptions = {
    'key' : 'bb81a277c29d23137366a5a415b29334',
    'format' : 'iframe',
    'height' : 50,
    'width' : 320,
    'params' : {}
  };
</script>
<script type="text/javascript" src="https://www.highperformanceformat.com/bb81a277c29d23137366a5a415b29334/invoke.js"></script>
</body>
</html>
"""

@Composable
fun AdsterraTopBanner(
    modifier: Modifier = Modifier,
    adsterraHtmlSnippet: String = DEFAULT_ADSTERRA_320X50_HTML
) {
    AdsterraBannerContainer(
        slotTag = "adsterra_top_banner",
        adsterraHtmlSnippet = adsterraHtmlSnippet,
        modifier = modifier
    )
}

@Composable
fun AdsterraBottomBanner(
    modifier: Modifier = Modifier,
    adsterraHtmlSnippet: String = DEFAULT_ADSTERRA_320X50_HTML
) {
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
                            settings.javaScriptCanOpenWindowsAutomatically = true
                            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            settings.userAgentString =
                                "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36"
                            CookieManager.getInstance().setAcceptCookie(true)
                            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                            loadDataWithBaseURL(
                                "https://nexus-ai-oficial.netlify.app/",
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
