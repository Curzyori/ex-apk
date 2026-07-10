package com.curzyori.exapk.ui.theme

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
import com.curzyori.exapk.data.model.ThemeMode

private val LightColorScheme = lightColorScheme(
    primary = Gray500,
    onPrimary = White,
    primaryContainer = Gray50,
    onPrimaryContainer = Gray900,
    secondary = Gray700,
    onSecondary = White,
    tertiary = Tertiary,
    onTertiary = White,
    background = White,
    onBackground = Gray900,
    surface = White,
    onSurface = Gray900,
    surfaceVariant = Gray50,
    onSurfaceVariant = Gray700,
    error = Error,
    onError = White
)

private val DarkColorScheme = darkColorScheme(
    primary = Gray500,
    onPrimary = White,
    primaryContainer = Gray800,
    onPrimaryContainer = Gray50,
    secondary = Gray400,
    onSecondary = Gray900,
    tertiary = TertiaryDark,
    onTertiary = Gray900,
    background = Gray900,
    onBackground = White,
    surface = Gray900,
    onSurface = White,
    surfaceVariant = Gray800,
    onSurfaceVariant = Gray50,
    error = Error,
    onError = White
)

@Composable
fun ExAPKTheme(
    themeMode: ThemeMode = ThemeMode.AUTO,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.AUTO -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
