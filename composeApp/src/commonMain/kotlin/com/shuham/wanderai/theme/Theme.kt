package com.shuham.wanderai.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = OceanTeal,
    onPrimary = White,
    primaryContainer = SageGreen,
    onPrimaryContainer = DarkText,
    
    secondary = SunsetCoral,
    onSecondary = White,
    secondaryContainer = SunsetCoral.copy(alpha = 0.2f),
    
    tertiary = SageGreen,
    onTertiary = DarkText,

    background = OffWhite, // Slightly off-white for depth
    onBackground = DarkText,
    
    surface = White,
    onSurface = DarkText,
    
    surfaceVariant = AliceBlue,
    onSurfaceVariant = MediumText,
    
    error = BurntRed,
    onError = White
)

private val DarkColorScheme = darkColorScheme(
    primary = CyanTeal,
    onPrimary = DeepVoid,
    primaryContainer = OceanTeal,
    onPrimaryContainer = LightText,
    
    secondary = SoftCoral,
    onSecondary = DeepVoid,
    
    tertiary = LightSage,
    onTertiary = DeepVoid,

    background = DeepVoid,
    onBackground = LightText,
    
    surface = DarkSurface,
    onSurface = LightText,
    
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DimText,
    
    error = BurntRed,
    onError = LightText
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
