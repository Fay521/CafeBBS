package com.bettafish.flarent.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.bettafish.flarent.App
import com.bettafish.flarent.utils.appSettings

@Composable
fun FlarentTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Cafe custom theme — use forum's primary color
        themeMode == AppThemeMode.CAFE_LIGHT -> {
            val forumPrimary: String? = remember { App.INSTANCE.appSettings.forum?.themePrimaryColor }
            getCafeLightScheme(forumPrimary)
        }
        themeMode == AppThemeMode.CAFE_DARK -> {
            val forumPrimary: String? = remember { App.INSTANCE.appSettings.forum?.themePrimaryColor }
            getCafeDarkScheme(forumPrimary)
        }

        // Default Material3 behavior for SYSTEM / LIGHT / DARK
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            val darkTheme = when (themeMode) {
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppThemeMode.DARK -> true
                AppThemeMode.LIGHT -> false
                AppThemeMode.CAFE_LIGHT -> false
                AppThemeMode.CAFE_DARK -> true
            }
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        themeMode == AppThemeMode.DARK -> darkColorScheme()
        else -> lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
