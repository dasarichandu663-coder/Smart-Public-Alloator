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
    primary = Color(0xFF6BDBAA),
    onPrimary = Color(0xFF003824),
    primaryContainer = CivicGreenPrimary,
    onPrimaryContainer = Color(0xFFB7F2D2),
    secondary = Color(0xFF90CDF4),
    onSecondary = Color(0xFF003353),
    secondaryContainer = CivicBlueSecondary,
    onSecondaryContainer = Color(0xFFCCE4F7),
    tertiary = Color(0xFFFBBF24),
    background = Color(0xFF111413),
    onBackground = Color(0xFFE1E3DF),
    surface = Color(0xFF191C1B),
    onSurface = Color(0xFFE1E3DF),
    surfaceVariant = Color(0xFF232825),
    onSurfaceVariant = Color(0xFFC0C7C1),
    outline = Color(0xFF8A938C)
)

private val LightColorScheme = lightColorScheme(
    primary = CivicGreenPrimary,
    onPrimary = CivicGreenOnPrimary,
    primaryContainer = CivicGreenPrimaryContainer,
    onPrimaryContainer = CivicGreenOnPrimaryContainer,
    secondary = CivicBlueSecondary,
    onSecondary = CivicBlueOnSecondary,
    secondaryContainer = CivicBlueSecondaryContainer,
    onSecondaryContainer = CivicBlueOnSecondaryContainer,
    tertiary = CivicAmberTertiary,
    tertiaryContainer = CivicAmberContainer,
    onTertiaryContainer = CivicAmberOnContainer,
    background = CivicBackground,
    onBackground = CivicOnBackground,
    surface = CivicSurface,
    onSurface = CivicOnSurface,
    surfaceVariant = CivicSurfaceVariant,
    onSurfaceVariant = CivicOnSurfaceVariant,
    outline = CivicOutline,
    outlineVariant = CivicOutlineVariant
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our polished Civic Theme for consistent branding
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
