package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = NavyOnPrimary,
    primaryContainer = NavyPrimaryContainer,
    onPrimaryContainer = NavyOnPrimaryContainer,
    secondary = EmeraldAccent,
    onSecondary = EmeraldOnAccent,
    secondaryContainer = EmeraldAccentContainer,
    onSecondaryContainer = EmeraldOnAccentContainer,
    tertiary = TealTertiary,
    background = NeutralLightBackground,
    surface = NeutralLightSurface,
    surfaceVariant = NeutralLightSurfaceVariant,
    onBackground = NeutralLightTextPrimary,
    onSurface = NeutralLightTextPrimary,
    outline = NeutralLightOutline,
    error = RedError,
    errorContainer = RedErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = NavyPrimaryDark,
    onPrimary = NavyOnPrimaryDark,
    primaryContainer = NavyPrimaryContainerDark,
    onPrimaryContainer = NavyOnPrimary,
    secondary = EmeraldAccentDark,
    onSecondary = EmeraldOnAccent,
    background = NeutralDarkBackground,
    surface = NeutralDarkSurface,
    surfaceVariant = NeutralDarkSurfaceVariant,
    onBackground = NeutralDarkTextPrimary,
    onSurface = NeutralDarkTextPrimary,
    outline = NeutralDarkOutline
)

@Composable
fun PorashonaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep educational branding consistent
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
) = PorashonaTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
