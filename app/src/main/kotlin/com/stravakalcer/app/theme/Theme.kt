package com.stravakalcer.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = KalcerOrange,
    onPrimary = Color.White,
    primaryContainer = KalcerOrangeDark,
    onPrimaryContainer = Color.White,
    secondary = KalcerCyan,
    onSecondary = Color.Black,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = KalcerCyan,
    tertiary = KalcerLime,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder
)

@Composable
fun StravaKalcerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}
