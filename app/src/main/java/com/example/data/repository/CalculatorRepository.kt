package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.CalculationHistory
import com.example.data.model.CalculatorSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class CalculatorRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("calculator_prefs", Context.MODE_PRIVATE)

    private val _historyFlow = MutableStateFlow<List<CalculationHistory>>(emptyList())
    val historyFlow: StateFlow<List<CalculationHistory>> = _historyFlow.asStateFlow()

    private val _settingsFlow = MutableStateFlow(CalculatorSettings())
    val settingsFlow: StateFlow<CalculatorSettings> = _settingsFlow.asStateFlow()

    init {
        loadSettings()
        loadHistory()
    }

    private fun loadSettings() {
        val isDark = prefs.getBoolean(KEY_DARK_THEME, true)
        val haptic = prefs.getBoolean(KEY_HAPTIC, true)
        val precision = prefs.getInt(KEY_PRECISION, 4)
        _settingsFlow.value = CalculatorSettings(
            isDarkTheme = isDark,
            isHapticFeedbackEnabled = haptic,
            decimalPrecision = precision
        )
    }

    fun updateSettings(settings: CalculatorSettings) {
        _settingsFlow.value = settings
        prefs.edit()
            .putBoolean(KEY_DARK_THEME, settings.isDarkTheme)
            .putBoolean(KEY_HAPTIC, settings.isHapticFeedbackEnabled)
            .putInt(KEY_PRECISION, settings.decimalPrecision)
            .apply()
    }

    private fun loadHistory() {
        val jsonString = prefs.getString(KEY_HISTORY, null) ?: return
        try {
            val array = JSONArray(jsonString)
            val list = mutableListOf<CalculationHistory>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    CalculationHistory(
                        id = obj.optString("id"),
                        expression = obj.optString("expression"),
                        result = obj.optString("result"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            _historyFlow.value = list
        } catch (_: Exception) {
            _historyFlow.value = emptyList()
        }
    }

    fun addHistory(expression: String, result: String) {
        val newItem = CalculationHistory(
            expression = expression,
            result = result
        )
        // Keep up to 100 recent items
        val updated = listOf(newItem) + _historyFlow.value.take(99)
        _historyFlow.value = updated
        saveHistory(updated)
    }

    fun clearHistory() {
        _historyFlow.value = emptyList()
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    private fun saveHistory(list: List<CalculationHistory>) {
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("expression", item.expression)
                put("result", item.result)
                put("timestamp", item.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, array.toString()).apply()
    }

    companion object {
        private const val KEY_DARK_THEME = "pref_dark_theme"
        private const val KEY_HAPTIC = "pref_haptic"
        private const val KEY_PRECISION = "pref_precision"
        private const val KEY_HISTORY = "pref_history_items"
    }
}
