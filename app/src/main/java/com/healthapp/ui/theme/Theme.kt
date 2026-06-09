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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * 根据莫兰迪配色方案生成 Material3 颜色方案
 */
@Composable
fun createLightColorScheme(scheme: MutedColorScheme) = lightColorScheme(
    primary = scheme.primary,
    onPrimary = Color.White,
    primaryContainer = scheme.primaryContainer,
    onPrimaryContainer = scheme.primaryDark,
    secondary = scheme.primaryLight,
    onSecondary = Color.White,
    secondaryContainer = scheme.primaryContainer,
    onSecondaryContainer = scheme.primaryDark,
    tertiary = scheme.success,
    onTertiary = Color.White,
    tertiaryContainer = scheme.success.copy(alpha = 0.12f),
    onTertiaryContainer = scheme.success,
    background = scheme.background,
    onBackground = scheme.textPrimary,
    surface = scheme.surface,
    onSurface = scheme.textPrimary,
    surfaceVariant = scheme.surfaceVariant,
    onSurfaceVariant = scheme.textSecondary,
    outline = scheme.textSecondary.copy(alpha = 0.5f),
    error = scheme.error,
    onError = Color.White
)

/**
 * 根据莫兰迪配色方案生成深色模式颜色方案
 */
@Composable
fun createDarkColorScheme(scheme: MutedColorScheme) = darkColorScheme(
    primary = scheme.primaryLight,
    onPrimary = scheme.primaryDark,
    primaryContainer = scheme.primaryDark,
    onPrimaryContainer = scheme.primaryContainer,
    secondary = scheme.primary,
    onSecondary = Color.White,
    secondaryContainer = scheme.primaryDark.copy(alpha = 0.7f),
    onSecondaryContainer = scheme.primaryLight,
    tertiary = scheme.success,
    onTertiary = Color.White,
    tertiaryContainer = scheme.success.copy(alpha = 0.2f),
    onTertiaryContainer = scheme.success,
    background = Color(0xFF1A1A1A),
    onBackground = Color(0xFFE5E5E5),
    surface = Color(0xFF2C2C2C),
    onSurface = Color(0xFFE5E5E5),
    surfaceVariant = Color(0xFF3C3C3C),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF808080),
    error = scheme.error.copy(alpha = 0.8f),
    onError = Color.White
)

/**
 * PulseVita 主题
 * 支持动态切换莫兰迪配色方案
 */
@Composable
fun HealthAppTheme(
    colorScheme: MutedColorScheme = MutedColorScheme.default,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val finalColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
        }
        darkTheme -> createDarkColorScheme(colorScheme)
        else -> createLightColorScheme(colorScheme)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        androidx.compose.runtime.SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = finalColorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = finalColorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * 获取当前配色方案的扩展函数
 * 用于在 Composable 中获取当前主题的颜色
 */
object PulseVitaTheme {
    /**
     * 获取当前配色方案
     */
    @Composable
    fun currentScheme(): MutedColorScheme {
        // 从 MaterialTheme 的主色调反推当前方案
        val primary = MaterialTheme.colorScheme.primary
        return MutedColorScheme.entries.find { scheme ->
            scheme.primary == primary
        } ?: MutedColorScheme.default
    }

    /**
     * 获取数据可视化颜色
     */
    @Composable
    fun chartColors(): List<Color> {
        val scheme = currentScheme()
        return listOf(
            scheme.chartBlue,
            scheme.chartGreen,
            scheme.chartOrange,
            scheme.chartPurple,
            scheme.chartTeal,
            scheme.chartRed,
            scheme.chartCyan,
            scheme.chartYellow
        )
    }

    /**
     * 获取心情颜色列表
     */
    @Composable
    fun moodColors(): List<Color> {
        val scheme = currentScheme()
        return listOf(
            scheme.mood1,
            scheme.mood2,
            scheme.mood3,
            scheme.mood4,
            scheme.mood5
        )
    }
}