package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.domain.CalculatorEngine

@Composable
fun CalculatorKeypad(
    isDarkTheme: Boolean,
    isHapticEnabled: Boolean,
    onDigitClick: (String) -> Unit,
    onOperatorClick: (String) -> Unit,
    onDotClick: () -> Unit,
    onEqualsClick: () -> Unit,
    onClearClick: () -> Unit,
    onToggleSignClick: () -> Unit,
    onPercentageClick: () -> Unit,
    onBackspaceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("calculator_keypad"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Row 1: Function buttons (C, +/-, %) in light gray, ÷ in vibrant orange circle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CalculatorButton(
                text = "C",
                type = ButtonType.FUNCTION,
                onClick = onClearClick,
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_clear"
            )
            CalculatorButton(
                text = "+/-",
                type = ButtonType.FUNCTION,
                onClick = onToggleSignClick,
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_toggle_sign"
            )
            CalculatorButton(
                text = "%",
                type = ButtonType.FUNCTION,
                onClick = onPercentageClick,
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_percent"
            )
            CalculatorButton(
                text = CalculatorEngine.OP_DIV,
                type = ButtonType.OPERATOR,
                onClick = { onOperatorClick(CalculatorEngine.OP_DIV) },
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_divide"
            )
        }

        // Row 2: 7, 8, 9 in dark gray rounded squares, × in vibrant orange circle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CalculatorButton(
                text = "7",
                type = ButtonType.NUMBER,
                onClick = { onDigitClick("7") },
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_7"
            )
            CalculatorButton(
                text = "8",
                type = ButtonType.NUMBER,
                onClick = { onDigitClick("8") },
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_8"
            )
            CalculatorButton(
                text = "9",
                type = ButtonType.NUMBER,
                onClick = { onDigitClick("9") },
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_9"
            )
            CalculatorButton(
                text = CalculatorEngine.OP_MUL,
                type = ButtonType.OPERATOR,
                onClick = { onOperatorClick(CalculatorEngine.OP_MUL) },
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_multiply"
            )
        }

        // Row 3: 4, 5, 6 in dark gray rounded squares, - in vibrant orange circle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CalculatorButton(
                text = "4",
                type = ButtonType.NUMBER,
                onClick = { onDigitClick("4") },
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_4"
            )
            CalculatorButton(
                text = "5",
                type = ButtonType.NUMBER,
                onClick = { onDigitClick("5") },
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_5"
            )
            CalculatorButton(
                text = "6",
                type = ButtonType.NUMBER,
                onClick = { onDigitClick("6") },
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_6"
            )
            CalculatorButton(
                text = CalculatorEngine.OP_SUB,
                type = ButtonType.OPERATOR,
                onClick = { onOperatorClick(CalculatorEngine.OP_SUB) },
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_subtract"
            )
        }

        // Row 4: 1, 2, 3 in dark gray rounded squares, + in vibrant orange circle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CalculatorButton(
                text = "1",
                type = ButtonType.NUMBER,
                onClick = { onDigitClick("1") },
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_1"
            )
            CalculatorButton(
                text = "2",
                type = ButtonType.NUMBER,
                onClick = { onDigitClick("2") },
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_2"
            )
            CalculatorButton(
                text = "3",
                type = ButtonType.NUMBER,
                onClick = { onDigitClick("3") },
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_3"
            )
            CalculatorButton(
                text = CalculatorEngine.OP_ADD,
                type = ButtonType.OPERATOR,
                onClick = { onOperatorClick(CalculatorEngine.OP_ADD) },
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_add"
            )
        }

        // Row 5: 0, ., ⌫ (backspace), = in vibrant orange circle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CalculatorButton(
                text = "0",
                type = ButtonType.NUMBER,
                onClick = { onDigitClick("0") },
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_0"
            )
            CalculatorButton(
                text = ".",
                type = ButtonType.NUMBER,
                onClick = onDotClick,
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_dot"
            )
            CalculatorButton(
                text = "⌫",
                type = ButtonType.FUNCTION,
                onClick = onBackspaceClick,
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_backspace"
            )
            CalculatorButton(
                text = "=",
                type = ButtonType.OPERATOR,
                onClick = onEqualsClick,
                isDarkTheme = isDarkTheme,
                isHapticEnabled = isHapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_equals"
            )
        }
    }
}
