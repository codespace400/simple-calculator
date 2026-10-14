package com.example.data.model

data class CalculatorSettings(
    val isDarkTheme: Boolean = true,
    val isHapticFeedbackEnabled: Boolean = true,
    val decimalPrecision: Int = 4 // 2, 4, 6, 8, or -1 for Auto
)
