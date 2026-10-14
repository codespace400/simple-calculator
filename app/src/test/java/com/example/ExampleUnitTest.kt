package com.example

import com.example.domain.CalculationResult
import com.example.domain.CalculatorEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    val result = CalculatorEngine.evaluate("2 + 2")
    assertTrue(result is CalculationResult.Success)
    assertEquals("4", (result as CalculationResult.Success).formatted)
  }

  @Test
  fun precedence_isCorrect() {
    val result = CalculatorEngine.evaluate("2 + 3 × 4")
    assertTrue(result is CalculationResult.Success)
    assertEquals("14", (result as CalculationResult.Success).formatted)
  }

  @Test
  fun divisionByZero_isHandled() {
    val result = CalculatorEngine.evaluate("10 ÷ 0")
    assertTrue(result is CalculationResult.Error)
    assertEquals("Cannot divide by 0", (result as CalculationResult.Error).message)
  }

  @Test
  fun percentage_isCorrect() {
    val result = CalculatorEngine.evaluate("50 %")
    assertTrue(result is CalculationResult.Success)
    assertEquals("0.5", (result as CalculationResult.Success).formatted)
  }

  @Test
  fun toggleSign_isCorrect() {
    val toggled = CalculatorEngine.toggleSign("42")
    assertEquals("(-42)", toggled)
    val toggledBack = CalculatorEngine.toggleSign(toggled)
    assertEquals("42", toggledBack)
  }
}
