package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val NexusDarkColorScheme = darkColorScheme(
    primary = NexusNeonBlue,
    onPrimary = Color(0xFF04131E),
    primaryContainer = Color(0xFF082F49),
    onPrimaryContainer = NexusNeonBlue,
    secondary = NexusEmerald,
    onSecondary = Color(0xFF031F16),
    secondaryContainer = Color(0xFF063829),
    onSecondaryContainer = NexusEmerald,
    tertiary = NexusAmber,
    onTertiary = Color(0xFF1E1302),
    background = NexusBgDeep,
    onBackground = NexusTextPrimary,
    surface = NexusSurface,
    onSurface = NexusTextPrimary,
    surfaceVariant = NexusSurfaceElevated,
    onSurfaceVariant = NexusTextSecondary,
    outline = NexusBorder,
    outlineVariant = Color(0xFF283548),
    error = NexusDangerRed,
    onError = Color.White,
    errorContainer = Color(0xFF3F1219),
    onErrorContainer = Color(0xFFFCA5A5)
)

private val NexusShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun NexusAiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NexusDarkColorScheme,
        typography = Typography,
        shapes = NexusShapes,
        content = content
    )
}
