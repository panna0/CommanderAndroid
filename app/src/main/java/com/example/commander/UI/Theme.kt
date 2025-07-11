package com.example.commander.UI

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    background = Background,
    error = Error,
    outline = Border,
    outlineVariant = BorderLight,
    surfaceVariant = BackgroundSecondary,
    onSurfaceVariant = BorderLight,
    onBackground = Secondary,

)

@Composable
fun CommanderTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,

    )
}
