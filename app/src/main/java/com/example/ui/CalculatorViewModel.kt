package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CalculationHistory
import com.example.data.model.CalculatorSettings
import com.example.data.repository.CalculatorRepository
import com.example.domain.CalculationResult
import com.example.domain.CalculatorEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val expression: String = "",
    val resultText: String = "0",
    val isEvaluated: Boolean = false,
    val isHistoryOpen: Boolean = false,
    val isSettingsOpen: Boolean = false,
    val errorMessage: String? = null
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CalculatorRepository(application.applicationContext)

    val historyList: StateFlow<List<CalculationHistory>> = repository.historyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<CalculatorSettings> = repository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalculatorSettings())

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun onDigit(digit: String) {
        val current = _uiState.value
        val newExpr = if (current.isEvaluated) {
            // Fresh start if previous expression was completed
            digit
        } else {
            CalculatorEngine.appendInput(current.expression, digit)
        }

        val preview = calculatePreview(newExpr)
        _uiState.value = current.copy(
            expression = newExpr,
            resultText = preview,
            isEvaluated = false,
            errorMessage = null
        )
    }

    fun onDot() {
        val current = _uiState.value
        val newExpr = if (current.isEvaluated) {
            "0."
        } else {
            CalculatorEngine.appendInput(current.expression, ".")
        }
        val preview = calculatePreview(newExpr)
        _uiState.value = current.copy(
            expression = newExpr,
            resultText = preview,
            isEvaluated = false,
            errorMessage = null
        )
    }

    fun onOperator(op: String) {
        val current = _uiState.value
        val baseExpr = if (current.isEvaluated) {
            // Chain calculation from previous result
            current.resultText.replace(",", "")
        } else if (current.expression.isBlank()) {
            current.resultText.replace(",", "")
        } else {
            current.expression
        }

        val newExpr = CalculatorEngine.appendOperator(baseExpr, op)
        val preview = calculatePreview(newExpr)

        _uiState.value = current.copy(
            expression = newExpr,
            resultText = preview,
            isEvaluated = false,
            errorMessage = null
        )
    }

    fun onEquals() {
        val current = _uiState.value
        if (current.expression.isBlank() || current.isEvaluated) return

        val precision = settings.value.decimalPrecision
        when (val res = CalculatorEngine.evaluate(current.expression, precision)) {
            is CalculationResult.Success -> {
                // Add to persistent history
                repository.addHistory(current.expression, res.formatted)
                _uiState.value = current.copy(
                    resultText = res.formatted,
                    isEvaluated = true,
                    errorMessage = null
                )
            }
            is CalculationResult.Error -> {
                _uiState.value = current.copy(
                    resultText = res.message,
                    isEvaluated = true,
                    errorMessage = res.message
                )
            }
        }
    }

    fun onClear() {
        _uiState.value = _uiState.value.copy(
            expression = "",
            resultText = "0",
            isEvaluated = false,
            errorMessage = null
        )
    }

    fun onToggleSign() {
        val current = _uiState.value
        val target = if (current.isEvaluated) current.resultText.replace(",", "") else current.expression
        val toggled = CalculatorEngine.toggleSign(target)
        val preview = calculatePreview(toggled)

        _uiState.value = current.copy(
            expression = toggled,
            resultText = preview,
            isEvaluated = false,
            errorMessage = null
        )
    }

    fun onPercentage() {
        val current = _uiState.value
        val target = if (current.isEvaluated) current.resultText.replace(",", "") else current.expression
        val withPercent = CalculatorEngine.applyPercentage(target)
        val preview = calculatePreview(withPercent)

        _uiState.value = current.copy(
            expression = withPercent,
            resultText = preview,
            isEvaluated = false,
            errorMessage = null
        )
    }

    fun onBackspace() {
        val current = _uiState.value
        if (current.isEvaluated) {
            onClear()
            return
        }

        val updated = CalculatorEngine.backspace(current.expression)
        val preview = calculatePreview(updated)

        _uiState.value = current.copy(
            expression = if (updated == "0") "" else updated,
            resultText = preview,
            errorMessage = null
        )
    }

    private fun calculatePreview(expr: String): String {
        if (expr.isBlank()) return "0"
        val precision = settings.value.decimalPrecision
        return when (val res = CalculatorEngine.evaluate(expr, precision)) {
            is CalculationResult.Success -> res.formatted
            is CalculationResult.Error -> _uiState.value.resultText
        }
    }

    fun setHistoryOpen(isOpen: Boolean) {
        _uiState.value = _uiState.value.copy(isHistoryOpen = isOpen)
    }

    fun setSettingsOpen(isOpen: Boolean) {
        _uiState.value = _uiState.value.copy(isSettingsOpen = isOpen)
    }

    fun reuseHistoryItem(item: CalculationHistory, loadAsExpression: Boolean) {
        if (loadAsExpression) {
            val preview = calculatePreview(item.expression)
            _uiState.value = _uiState.value.copy(
                expression = item.expression,
                resultText = preview,
                isEvaluated = false,
                isHistoryOpen = false
            )
        } else {
            // Load result
            _uiState.value = _uiState.value.copy(
                expression = item.result.replace(",", ""),
                resultText = item.result,
                isEvaluated = true,
                isHistoryOpen = false
            )
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun updateSettings(newSettings: CalculatorSettings) {
        viewModelScope.launch {
            repository.updateSettings(newSettings)
            // Re-evaluate current display with updated precision
            val current = _uiState.value
            if (current.expression.isNotBlank()) {
                val preview = calculatePreview(current.expression)
                _uiState.value = current.copy(resultText = preview)
            }
        }
    }
}
