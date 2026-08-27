package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SleekColorScheme = lightColorScheme(
    primary = SleekTeal,
    onPrimary = SleekTextWhite,
    primaryContainer = SleekTealMuted,
    onPrimaryContainer = SleekTealDark,
    secondary = EcoEmerald,
    onSecondary = SleekTextWhite,
    secondaryContainer = EcoEmerald.copy(alpha = 0.2f),
    onSecondaryContainer = EcoEmeraldDark,
    tertiary = CyberCyan,
    background = SleekBackground,
    onBackground = SleekTextDark,
    surface = SleekCard,
    onSurface = SleekTextDark,
    surfaceVariant = SleekCardSecondary,
    onSurfaceVariant = SleekTextMuted,
    outline = SleekCardBorder,
    error = DangerRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = SleekColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SleekTeal.toArgb()
            window.navigationBarColor = SleekCard.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
