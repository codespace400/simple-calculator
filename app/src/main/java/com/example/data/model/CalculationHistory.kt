package com.example.data.model

import java.util.UUID

data class CalculationHistory(
    val id: String = UUID.randomUUID().toString(),
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)
