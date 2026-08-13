package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GeoBlueLight,
    onPrimary = GeoBackground,
    primaryContainer = GeoBlueContainer,
    onPrimaryContainer = GeoBlueLight,
    secondary = GeoBlue,
    onSecondary = GeoBackground,
    background = GeoBackground,
    onBackground = GeoTextPrimary,
    surface = GeoSurface,
    onSurface = GeoTextPrimary,
    surfaceVariant = GeoSurfaceVariant,
    onSurfaceVariant = GeoTextSecondary,
    outline = GeoTextMuted,
    outlineVariant = GeoBorderGlass
)

private val LightColorScheme = darkColorScheme( // Geometric Balance is a dark slate aesthetic
    primary = GeoBluePrimary,
    onPrimary = GeoTextPrimary,
    primaryContainer = GeoBlueContainer,
    onPrimaryContainer = GeoBlueLight,
    secondary = GeoBlue,
    onSecondary = GeoBackground,
    background = GeoBackground,
    onBackground = GeoTextPrimary,
    surface = GeoSurface,
    onSurface = GeoTextPrimary,
    surfaceVariant = GeoSurfaceVariant,
    onSurfaceVariant = GeoTextSecondary,
    outline = GeoTextMuted,
    outlineVariant = GeoBorderGlass
)

@Composable
fun CodeReaderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = CodeReaderTypography,
        content = content
    )
}
