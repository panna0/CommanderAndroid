package com.example.commander.UI


import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    titleLarge = TextStyle(
        fontSize = 32.sp,
        fontFamily = FontFamily.SansSerif

    ),
    headlineSmall = TextStyle(
        fontSize = 20.sp,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight(150)
    ),
    bodyMedium = TextStyle(
        fontSize = 16.sp
    ),
    labelSmall = TextStyle(
        fontSize = 12.sp
    )
)
