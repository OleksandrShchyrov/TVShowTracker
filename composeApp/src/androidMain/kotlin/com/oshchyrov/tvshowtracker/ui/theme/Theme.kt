package com.oshchyrov.tvshowtracker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.oshchyrov.tvshowtracker.domain.model.ThemeMode

// Vibrant indigo/amber palette
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBBC3FF),
    onPrimary = Color(0xFF1B2580),
    primaryContainer = Color(0xFF333D98),
    onPrimaryContainer = Color(0xFFDEE0FF),
    secondary = Color(0xFFFFB870),
    onSecondary = Color(0xFF4A2800),
    secondaryContainer = Color(0xFF6A3C00),
    onSecondaryContainer = Color(0xFFFFDDB8),
    tertiary = Color(0xFF9ECAFF),
    onTertiary = Color(0xFF003259),
    tertiaryContainer = Color(0xFF00497D),
    onTertiaryContainer = Color(0xFFD0E4FF),
    background = Color(0xFF121318),
    onBackground = Color(0xFFE3E1EC),
    surface = Color(0xFF121318),
    onSurface = Color(0xFFE3E1EC),
    surfaceVariant = Color(0xFF45464F),
    onSurfaceVariant = Color(0xFFC6C5D0),
    surfaceContainerHighest = Color(0xFF353640),
    surfaceContainer = Color(0xFF1E1F28),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    outline = Color(0xFF90909A),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4A54B0),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDEE0FF),
    onPrimaryContainer = Color(0xFF000F5C),
    secondary = Color(0xFF8B5000),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDDB8),
    onSecondaryContainer = Color(0xFF2C1600),
    tertiary = Color(0xFF0061A2),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD0E4FF),
    onTertiaryContainer = Color(0xFF001D36),
    background = Color(0xFFFCF8FF),
    onBackground = Color(0xFF1B1B21),
    surface = Color(0xFFFCF8FF),
    onSurface = Color(0xFF1B1B21),
    surfaceVariant = Color(0xFFE3E1EC),
    onSurfaceVariant = Color(0xFF45464F),
    surfaceContainerHighest = Color(0xFFE5E1EC),
    surfaceContainer = Color(0xFFF0ECF5),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    outline = Color(0xFF767680),
)

@Composable
fun TVShowTrackerTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
