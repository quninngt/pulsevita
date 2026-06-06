package com.healthapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = BrandGreen,
    onPrimary = TextOnPrimary,
    primaryContainer = BrandGreenContainer,
    onPrimaryContainer = BrandGreenDark,
    secondary = BrandGreenLight,
    onSecondary = TextOnPrimary,
    secondaryContainer = BrandGreenContainer,
    onSecondaryContainer = BrandGreenDark,
    tertiary = GreenHealthy,
    onTertiary = TextOnPrimary,
    tertiaryContainer = GreenHealthy.copy(alpha = 0.12f),
    onTertiaryContainer = GreenHealthy,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,
    outline = TextTertiary,
    error = StatusError,
    onError = TextOnPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandGreenLight,
    onPrimary = TextPrimary,
    primaryContainer = BrandGreenDark,
    onPrimaryContainer = BrandGreenContainer,
    secondary = BrandGreen,
    onSecondary = TextOnPrimary,
    secondaryContainer = BrandGreenDark.copy(alpha = 0.3f),
    onSecondaryContainer = BrandGreenLight,
    tertiary = GreenHealthy,
    onTertiary = TextPrimary,
    tertiaryContainer = GreenHealthy.copy(alpha = 0.2f),
    onTertiaryContainer = GreenHealthy,
    background = BackgroundDark,
    onBackground = TextOnDark,
    surface = SurfaceDark,
    onSurface = TextOnDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = TextSecondaryDark,
    error = Color(0xFFFF453A),
    onError = TextOnPrimary
)

@Composable
fun HealthAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
