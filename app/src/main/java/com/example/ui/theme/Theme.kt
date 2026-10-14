package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = OrangeOperator,
    onPrimary = TextWhite,
    primaryContainer = OrangeOperatorPressed,
    onPrimaryContainer = TextWhite,
    background = DarkBackground,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextGray,
)

private val LightColorScheme = lightColorScheme(
    primary = OrangeOperator,
    onPrimary = TextWhite,
    primaryContainer = OrangeOperatorPressed,
    onPrimaryContainer = TextWhite,
    background = LightBackground,
    onBackground = LightTextDark,
    surface = LightSurface,
    onSurface = LightTextDark,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextGray,
)

@Composable
fun CalculatorTheme(
    darkTheme: Boolean = true, // Default to dark theme as requested
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
