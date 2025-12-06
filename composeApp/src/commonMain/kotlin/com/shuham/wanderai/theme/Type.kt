package com.shuham.wanderai.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// NOTE: We will need to add the actual font files to the resources folder later.
// For now, we define the typography using default fonts as placeholders.
// This ensures the app compiles and runs.

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default, // Should be Montserrat
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default, // Should be Montserrat
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default, // Should be Montserrat
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default, // Should be Lato
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default, // Should be Lato
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
    )
)
