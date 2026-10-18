package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LuminaCyan,
    onPrimary = Color(0xFF071520),
    primaryContainer = Color(0xFF0E384D),
    onPrimaryContainer = Color(0xFFBCEEFF),
    secondary = LuminaAmber,
    onSecondary = Color(0xFF261800),
    secondaryContainer = Color(0xFF4A3400),
    onSecondaryContainer = Color(0xFFFFE0B2),
    tertiary = LuminaMagenta,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = OnSurfaceWhite,
    surface = DarkSurface,
    onSurface = OnSurfaceWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = OnSurfaceSecondary,
    outline = DarkOutline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Photography editors look best in dedicated dark studio theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
