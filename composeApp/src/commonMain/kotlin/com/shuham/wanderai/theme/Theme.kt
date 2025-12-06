package com.shuham.wanderai.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = OceanTeal,
    onPrimary = White,
    secondary = SunsetCoral,
    tertiary = SageGreen,
    background = AliceBlue,
    surface = White,
    error = BurntRed
)

private val DarkColorScheme = darkColorScheme(
    primary = CyanTeal,
    secondary = SoftCoral,
    background = DeepVoid,
    surface = DarkGrey
)

@Composable
fun WanderAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
