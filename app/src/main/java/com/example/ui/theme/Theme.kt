package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    colorTheme: AppColorTheme = AppColorTheme.OCEAN_BLUE,
    customColorHex: String = "#2563EB",
    fontSize: AppFontSize = AppFontSize.NORMAL,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val basePrimary = if (colorTheme == AppColorTheme.CUSTOM) {
        ColorUtils.parseHexColor(customColorHex)
    } else {
        colorTheme.primaryColor
    }

    val primary = ColorUtils.getAdaptivePrimary(basePrimary, darkTheme)

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = primary,
            onPrimary = Color(0xFF0F172A),
            primaryContainer = primary.copy(alpha = 0.28f),
            onPrimaryContainer = Color(0xFFDBEAFE),
            secondary = primary.copy(alpha = 0.85f),
            onSecondary = Color.White,
            secondaryContainer = DarkSurfaceVariant,
            onSecondaryContainer = Color(0xFF93C5FD),
            tertiary = SuccessGreen,
            onTertiary = Color.White,
            tertiaryContainer = DarkSuccessGreenContainer,
            onTertiaryContainer = Color(0xFF6EE7B7),
            background = DarkBg,
            onBackground = DarkTextPrimary,
            surface = DarkCardSurface,
            onSurface = DarkTextPrimary,
            surfaceVariant = DarkSurfaceVariant,
            onSurfaceVariant = DarkTextSecondary,
            outline = DarkBorder,
            outlineVariant = DarkSurfaceVariant
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = PureWhite,
            primaryContainer = primary.copy(alpha = 0.12f),
            onPrimaryContainer = primary,
            secondary = primary.copy(alpha = 0.85f),
            onSecondary = PureWhite,
            secondaryContainer = primary.copy(alpha = 0.18f),
            onSecondaryContainer = primary,
            tertiary = SuccessGreen,
            onTertiary = PureWhite,
            tertiaryContainer = SuccessGreenLight,
            onTertiaryContainer = Color(0xFF065F46),
            background = OffWhiteBg,
            onBackground = Slate900,
            surface = CardSurfaceLight,
            onSurface = Slate900,
            surfaceVariant = Slate100,
            onSurfaceVariant = Slate500,
            outline = Slate200,
            outlineVariant = Slate100
        )
    }

    val currentDensity = LocalDensity.current
    val customDensity = Density(
        density = currentDensity.density,
        fontScale = currentDensity.fontScale * fontSize.scaleFactor
    )

    CompositionLocalProvider(
        LocalDensity provides customDensity
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
