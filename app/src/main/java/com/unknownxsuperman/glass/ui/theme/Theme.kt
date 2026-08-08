package com.unknownxsuperman.glass.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = AppColors.Accent,
    background = AppColors.Bg,
    surface = AppColors.Surface1,
    onBackground = AppColors.TextPrimary,
    onSurface = AppColors.TextPrimary,
)

@Composable
fun GlassBrowserTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = AppTypography,
        content = content
    )
}
