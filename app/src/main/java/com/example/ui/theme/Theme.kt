package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = PureBlack,
    primaryContainer = NeonBlue,
    onPrimaryContainer = TextPrimary,
    secondary = NeonPurple,
    onSecondary = PureBlack,
    tertiary = AccentGold,
    background = OledBlack,
    onBackground = TextPrimary,
    surface = DeepMidnight,
    onSurface = TextPrimary,
    surfaceVariant = GlassSurfaceDark,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorderSubtle
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // Für TV & Philips OLED verwenden wir immer das optimierte Dark-Theme
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
