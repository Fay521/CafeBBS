package com.bettafish.flarent.ui.theme

import androidx.compose.ui.graphics.Color
import com.bettafish.flarent.utils.toComposeColor

private val WarmBrown = Color(0xFF7B5B3A)
private val WarmCream = Color(0xFFFFF8F0)

fun getCafeLightScheme(forumPrimary: String?): androidx.compose.material3.ColorScheme {
    val primary = forumPrimary?.toComposeColor() ?: WarmBrown
    return androidx.compose.material3.lightColorScheme(
        primary = primary,
        onPrimary = Color.White,
        primaryContainer = primary.copy(alpha = 0.15f),
        onPrimaryContainer = primary.copy(red = primary.red * 0.2f, green = primary.green * 0.2f, blue = primary.blue * 0.2f),
        secondary = primary.copy(alpha = 0.7f),
        onSecondary = Color.White,
        secondaryContainer = primary.copy(alpha = 0.1f),
        onSecondaryContainer = primary.copy(red = primary.red * 0.3f, green = primary.green * 0.3f, blue = primary.blue * 0.3f),
        tertiary = primary,
        onTertiary = Color.White,
        tertiaryContainer = primary.copy(alpha = 0.12f),
        onTertiaryContainer = primary.copy(red = primary.red * 0.25f, green = primary.green * 0.25f, blue = primary.blue * 0.25f),
        background = WarmCream,
        onBackground = Color(0xFF1C1B1A),
        surface = WarmCream,
        onSurface = Color(0xFF1C1B1A),
        surfaceVariant = Color(0xFFF0E8DD),
        onSurfaceVariant = Color(0xFF504840),
        surfaceContainerLow = Color(0xFFFBF5EC),
        surfaceContainerHigh = Color(0xFFF2EBE1),
        surfaceContainerHighest = Color(0xFFEDE5DA),
        outline = Color(0xFF807870),
        outlineVariant = Color(0xFFD0C8BF),
        error = Color(0xFFBA1A1A),
        onError = Color.White,
    )
}

fun getCafeDarkScheme(forumPrimary: String?): androidx.compose.material3.ColorScheme {
    val primary = forumPrimary?.toComposeColor() ?: WarmBrown
    return androidx.compose.material3.darkColorScheme(
        primary = primary.copy(alpha = 0.85f),
        onPrimary = Color(0xFF3A2000),
        primaryContainer = primary.copy(alpha = 0.25f),
        onPrimaryContainer = primary.copy(alpha = 0.9f),
        secondary = primary.copy(alpha = 0.65f),
        onSecondary = Color(0xFF2A1500),
        secondaryContainer = primary.copy(alpha = 0.2f),
        onSecondaryContainer = primary.copy(alpha = 0.8f),
        tertiary = primary.copy(alpha = 0.7f),
        onTertiary = Color(0xFF251000),
        tertiaryContainer = primary.copy(alpha = 0.18f),
        onTertiaryContainer = primary.copy(alpha = 0.75f),
        background = Color(0xFF141210),
        onBackground = Color(0xFFE8E2DC),
        surface = Color(0xFF141210),
        onSurface = Color(0xFFE8E2DC),
        surfaceVariant = Color(0xFF3D352D),
        onSurfaceVariant = Color(0xFFD0C8BF),
        surfaceContainerLow = Color(0xFF1A1714),
        surfaceContainerHigh = Color(0xFF24211C),
        surfaceContainerHighest = Color(0xFF2E2A25),
        outline = Color(0xFF989088),
        outlineVariant = Color(0xFF3D352D),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
    )
}
