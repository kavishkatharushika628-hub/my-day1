package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

enum class AppColorTheme(
    val displayName: String,
    val primaryColor: Color
) {
    OCEAN_BLUE("Ocean Blue", Color(0xFF2563EB)),
    NATURE_GREEN("Nature Green", Color(0xFF059669)),
    ROSE_PINK("Rose Pink", Color(0xFFE11D48)),
    LAVENDER("Lavender", Color(0xFF7C3AED)),
    SUNSHINE("Sunshine", Color(0xFFD97706)),
    SLATE_GREY("Slate Grey", Color(0xFF475569)),
    CUSTOM("Custom", Color(0xFF2563EB));

    companion object {
        fun fromName(name: String): AppColorTheme {
            return try {
                valueOf(name)
            } catch (_: Exception) {
                OCEAN_BLUE
            }
        }
    }
}

enum class AppFontSize(
    val displayName: String,
    val scaleFactor: Float,
    val description: String
) {
    SMALL("Small", 0.88f, "Compact text for more content on screen"),
    NORMAL("Normal", 1.0f, "Standard balanced readable size"),
    LARGE("Large", 1.15f, "Enlarged text for relaxed reading"),
    EXTRA_LARGE("Extra Large", 1.30f, "Maximum readability and high accessibility");

    companion object {
        fun fromName(name: String): AppFontSize {
            return try {
                valueOf(name)
            } catch (_: Exception) {
                NORMAL
            }
        }
    }
}

object ColorUtils {
    fun parseHexColor(hexString: String, defaultColor: Color = Color(0xFF2563EB)): Color {
        val clean = hexString.trim().removePrefix("#")
        if (clean.length != 6 && clean.length != 8) return defaultColor
        return try {
            val colorInt = android.graphics.Color.parseColor("#$clean")
            Color(colorInt)
        } catch (_: Exception) {
            defaultColor
        }
    }

    fun toHexString(color: Color): String {
        val argb = color.toArgb()
        return String.format("#%06X", 0xFFFFFF and argb)
    }

    fun getAdaptivePrimary(color: Color, isDark: Boolean): Color {
        if (!isDark) return color
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(color.toArgb(), hsv)
        // Ensure color has sufficient brightness in dark mode
        hsv[1] = (hsv[1] * 0.75f).coerceIn(0.2f, 0.75f)
        hsv[2] = (hsv[2] * 1.35f).coerceIn(0.78f, 0.98f)
        return Color(android.graphics.Color.HSVToColor(hsv))
    }
}
