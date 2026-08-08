package com.unknownxsuperman.glass.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

/**
 * FontFamily.Serif approximates the DM Serif Display wordmark used on the
 * desktop New Tab page without needing a downloadable-fonts round trip;
 * swap in a real DM Serif Display font resource later if you want an exact
 * match.
 */
val AppTypography = Typography(
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.Serif, fontSize = 22.sp),
)
