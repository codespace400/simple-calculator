package com.example.domain

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CalculatorEngine {

    const val OP_ADD = "+"
    const val OP_SUB = "-"
    const val OP_MUL = "×"
    const val OP_DIV = "÷"

    fun isOperator(char: Char): Boolean {
        return char == '+' || char == '-' || char == '×' || char == '÷'
    }

    fun isOperator(str: String): Boolean {
        return str == OP_ADD || str == OP_SUB || str == OP_MUL || str == OP_DIV
    }

    /**
     * Evaluates a mathematical expression string like "12.5 + 3 × (-4)"
     * Returns a Result containing the computed double or an error message.
     */
    fun evaluate(expression: String, precision: Int = 4): CalculationResult {
        val sanitized = sanitizeExpression(expression)
        if (sanitized.isBlank()) return CalculationResult.Success("0", 0.0)

        return try {
            val tokens = tokenize(sanitized)
            if (tokens.isEmpty()) return CalculationResult.Success("0", 0.0)

            val value = parseTokens(tokens)
            if (value.isInfinite() || value.isNaN()) {
                CalculationResult.Error("Error")
            } else {
                val formatted = formatNumber(value, precision)
                CalculationResult.Success(formatted, value)
            }
        } catch (e: ArithmeticException) {
            CalculationResult.Error(e.message ?: "Error")
        } catch (_: Exception) {
            CalculationResult.Error("Error")
        }
    }

    fun formatNumber(value: Double, precision: Int): String {
        if (value.isInfinite()) return "Overflow"
        if (value.isNaN()) return "Error"

        // Format without scientific notation for reasonable ranges
        val bd = try {
            BigDecimal(value.toString())
        } catch (_: Exception) {
            return value.toString()
        }

        return if (precision > 0) {
            val rounded = bd.setScale(precision, RoundingMode.HALF_UP).stripTrailingZeros()
            // Format cleanly
            val symbols = DecimalFormatSymbols(Locale.US)
            val pattern = if (rounded.scale() > 0) "#,##0.${"#".repeat(precision)}" else "#,##0"
            val df = DecimalFormat(pattern, symbols)
            df.format(rounded)
        } else {
            // Auto precision (up to 10 digits)
            val rounded = bd.setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
            val symbols = DecimalFormatSymbols(Locale.US)
            val pattern = if (rounded.scale() > 0) "#,##0.##########" else "#,##0"
            val df = DecimalFormat(pattern, symbols)
            df.format(rounded)
        }
    }

    private fun sanitizeExpression(expression: String): String {
        return expression.trim().replace(" ", "")
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        val length = expr.length

        while (i < length) {
            val ch = expr[i]

            when {
                ch == '(' -> {
                    // Check if it's a negative number like (-123.4)
                    if (i + 1 < length && expr[i + 1] == '-') {
                        val closeParen = expr.indexOf(')', i)
                        if (closeParen != -1) {
                            val innerNum = expr.substring(i + 1, closeParen)
                            tokens.add(innerNum) // e.g. "-123.4"
                            i = closeParen + 1
                            continue
                        }
                    }
                    tokens.add("(")
                    i++
                }
                ch == ')' -> {
                    tokens.add(")")
                    i++
                }
                isOperator(ch) -> {
                    // Check if it's a leading negative sign at start of expression
                    if (ch == '-' && (tokens.isEmpty() || isOperator(tokens.last()) || tokens.last() == "(")) {
                        // Unary minus for the upcoming number
                        val start = i
                        i++
                        while (i < length && (expr[i].isDigit() || expr[i] == '.')) {
                            i++
                        }
                        if (i > start + 1) {
                            tokens.add(expr.substring(start, i))
                        } else {
                            tokens.add("-1")
                            tokens.add(OP_MUL)
                        }
                    } else {
                        tokens.add(ch.toString())
                        i++
                    }
                }
                ch == '%' -> {
                    tokens.add("%")
                    i++
                }
                ch.isDigit() || ch == '.' -> {
                    val start = i
                    while (i < length && (expr[i].isDigit() || expr[i] == '.')) {
                        i++
                    }
                    tokens.add(expr.substring(start, i))
                }
                else -> {
                    i++
                }
            }
        }
        return tokens
    }

    private fun parseTokens(tokens: List<String>): Double {
        // If last token is an operator, ignore it for evaluation preview
        val cleanTokens = if (tokens.isNotEmpty() && isOperator(tokens.last())) {
            tokens.dropLast(1)
        } else {
            tokens
        }

        if (cleanTokens.isEmpty()) return 0.0

        // Step 1: Handle percentages: replace "x %" with "(x / 100)"
        val withPercents = mutableListOf<String>()
        var idx = 0
        while (idx < cleanTokens.size) {
            val token = cleanTokens[idx]
            if (token == "%") {
                if (withPercents.isNotEmpty()) {
                    val prev = withPercents.removeAt(withPercents.size - 1).toDoubleOrNull() ?: 0.0
                    withPercents.add((prev / 100.0).toString())
                }
            } else {
                withPercents.add(token)
            }
            idx++
        }

        // Step 2: Handle multiplication and division (left-to-right precedence)
        val afterMulDiv = mutableListOf<String>()
        var j = 0
        while (j < withPercents.size) {
            val token = withPercents[j]
            if (token == OP_MUL || token == OP_DIV) {
                if (j + 1 < withPercents.size && afterMulDiv.isNotEmpty()) {
                    val left = afterMulDiv.removeAt(afterMulDiv.size - 1).toDoubleOrNull() ?: 0.0
                    val right = withPercents[j + 1].toDoubleOrNull() ?: 0.0

                    if (token == OP_DIV) {
                        if (right == 0.0) throw ArithmeticException("Cannot divide by 0")
                        afterMulDiv.add((left / right).toString())
                    } else {
                        afterMulDiv.add((left * right).toString())
                    }
                    j += 2
                    continue
                }
            }
            afterMulDiv.add(token)
            j++
        }

        // Step 3: Handle addition and subtraction
        if (afterMulDiv.isEmpty()) return 0.0
        var total = afterMulDiv[0].toDoubleOrNull() ?: 0.0
        var k = 1
        while (k < afterMulDiv.size) {
            val op = afterMulDiv[k]
            val nextVal = if (k + 1 < afterMulDiv.size) {
                afterMulDiv[k + 1].toDoubleOrNull() ?: 0.0
            } else 0.0

            when (op) {
                OP_ADD -> total += nextVal
                OP_SUB -> total -= nextVal
            }
            k += 2
        }

        return total
    }

    /**
     * Toggles the sign of the current operand in the expression.
     */
    fun toggleSign(expr: String): String {
        if (expr.isEmpty() || expr == "0") return expr

        // Find last number operand
        val tokens = tokenize(sanitizeExpression(expr)).toMutableList()
        if (tokens.isEmpty()) return expr

        val lastIndex = tokens.size - 1
        val lastToken = tokens[lastIndex]

        if (isOperator(lastToken)) {
            // Nothing to toggle if ending with operator
            return expr
        }

        // If it's a number
        val num = lastToken.toDoubleOrNull()
        if (num != null) {
            if (lastToken.startsWith("-")) {
                tokens[lastIndex] = lastToken.removePrefix("-")
            } else {
                tokens[lastIndex] = "(-$lastToken)"
            }
            return rebuildExpression(tokens)
        }

        return expr
    }

    /**
     * Converts the current last operand to percentage (dividing by 100).
     */
    fun applyPercentage(expr: String): String {
        if (expr.isEmpty() || expr == "0") return "0"
        val trimmed = expr.trim()
        if (trimmed.endsWith(OP_ADD) || trimmed.endsWith(OP_SUB) ||
            trimmed.endsWith(OP_MUL) || trimmed.endsWith(OP_DIV)) {
            return expr
        }
        return "$expr %"
    }

    /**
     * Appends a digit or decimal point to the expression with validation.
     */
    fun appendInput(current: String, input: String): String {
        if (current == "0" && input != ".") {
            return input
        }

        if (input == ".") {
            // Find current number part after last operator
            val lastPart = current.split(Regex("[+\\-×÷]")).lastOrNull() ?: ""
            if (lastPart.contains(".")) {
                return current // Already has dot in this operand
            }
            if (current.isEmpty() || isOperator(current.last())) {
                return current + "0."
            }
            return current + "."
        }

        return current + input
    }

    /**
     * Appends an operator, replacing the previous operator if consecutively typed.
     */
    fun appendOperator(current: String, op: String): String {
        if (current.isEmpty()) {
            return if (op == OP_SUB) "-" else "0 $op "
        }

        val trimmed = current.trimEnd()
        if (isOperator(trimmed.last())) {
            // Replace operator
            val withoutLast = trimmed.dropLast(1).trimEnd()
            return "$withoutLast $op "
        }

        return "$trimmed $op "
    }

    /**
     * Backspace deletes one character, cleanly handling parentheses or spaced operators.
     */
    fun backspace(expr: String): String {
        if (expr.isEmpty() || expr == "0") return "0"
        val trimmed = expr.trimEnd()
        if (trimmed.endsWith(")")) {
            // If ending with (-number), unwrap it
            val lastOpen = trimmed.lastIndexOf("(-")
            if (lastOpen != -1) {
                val inner = trimmed.substring(lastOpen + 2, trimmed.length - 1)
                return trimmed.substring(0, lastOpen) + inner
            }
        }

        val dropped = trimmed.dropLast(1).trimEnd()
        return if (dropped.isEmpty()) "0" else dropped
    }

    private fun rebuildExpression(tokens: List<String>): String {
        val sb = StringBuilder()
        for (i in tokens.indices) {
            val token = tokens[i]
            if (isOperator(token)) {
                sb.append(" ").append(token).append(" ")
            } else {
                sb.append(token)
            }
        }
        return sb.toString().trim()
    }
}

sealed class CalculationResult {
    data class Success(val formatted: String, val value: Double) : CalculationResult()
    data class Error(val message: String) : CalculationResult()
}
