package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = RoyalPurple,
    onPrimary = NavyDeep,
    primaryContainer = NavyCardHighlight,
    onPrimaryContainer = ElectricCyan,
    secondary = ElectricCyan,
    onSecondary = NavyDeep,
    secondaryContainer = NavyCard,
    onSecondaryContainer = Color.White,
    tertiary = BrightAmber,
    onTertiary = NavyDeep,
    tertiaryContainer = Color(0xFF332008),
    onTertiaryContainer = BrightAmber,
    background = NavyDeep,
    onBackground = TextPrimaryDark,
    surface = NavyDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = NavySurface,
    onSurfaceVariant = TextSecondaryDark,
    outline = NavyCardBorder,
    error = CrimsonAlert,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = LightPrimary,
    secondary = LightSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = LightSecondary,
    tertiary = LightAmber,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF92400E),
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightCardHighlight,
    onSurfaceVariant = LightTextSecondary,
    outline = LightCardBorder,
    error = LightError,
    onError = Color.White
)

@Composable
fun GatexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    GatexTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
