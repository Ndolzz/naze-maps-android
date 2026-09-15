package com.naze.maps.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColors = darkColorScheme(
    primary = NazePrimary,
    secondary = NazePurple,
    background = NazeBgDark,
    surface = NazeSurfaceDark,
    surfaceVariant = NazeSurfaceDark2,
    onBackground = NazeTextDark,
    onSurface = NazeTextDark,
    error = NazeDanger,
)

private val LightColors = lightColorScheme(
    primary = NazePrimary,
    secondary = NazePurple,
    background = NazeBgLight,
    surface = NazeSurfaceLight,
    surfaceVariant = NazeSurfaceLight2,
    onBackground = NazeTextLight,
    onSurface = NazeTextLight,
    error = NazeDanger,
)

/**
 * Theme util shared with dark/light setting from SettingsDataStore.
 * darkTheme param is driven by the user's saved preference, defaulting to system.
 */
@Composable
fun NazeMapsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.navigationBarColor = colorScheme.background.toArgb()
            }
        }
    }
    MaterialTheme(colorScheme = colorScheme, typography = NazeTypography, content = content)
}
