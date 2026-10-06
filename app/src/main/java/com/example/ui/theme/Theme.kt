package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricPurple,
    onPrimary = Color.White,
    primaryContainer = SurfaceNavy,
    onPrimaryContainer = CyanAccent,
    secondary = CyanAccent,
    onSecondary = DeepNavyBg,
    tertiary = NeonBlue,
    background = DeepNavyBg,
    onBackground = TextWhite,
    surface = CardNavy,
    onSurface = TextWhite,
    surfaceVariant = SurfaceNavy,
    onSurfaceVariant = TextMuted,
    outline = CardNavyBorder
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = ElectricPurple,
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    tertiary = NeonBlue,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = LightTextSecondary,
    outline = LightSurfaceBorder
)

@Composable
fun MooreMealyTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
