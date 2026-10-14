package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LightTextDark
import com.example.ui.theme.LightTextGray
import com.example.ui.theme.OrangeOperator
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite

@Composable
fun CalculatorDisplay(
    expression: String,
    resultText: String,
    isDarkTheme: Boolean,
    onSwipeDown: () -> Unit,
    modifier: Modifier = Modifier
) {
    var totalDragY by remember { mutableFloatStateOf(0f) }
    val exprScrollState = rememberScrollState()

    // Automatically scroll to end as expression grows
    LaunchedEffect(expression) {
        if (expression.isNotEmpty()) {
            exprScrollState.animateScrollTo(exprScrollState.maxValue)
        }
    }

    val textColorResult = if (isDarkTheme) TextWhite else LightTextDark
    val textColorExpression = if (isDarkTheme) TextGray else LightTextGray

    // Dynamic font size for result based on length
    val resultFontSize = when {
        resultText.length > 14 -> 32.sp
        resultText.length > 10 -> 40.sp
        resultText.length > 7 -> 48.sp
        else -> 56.sp
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { totalDragY = 0f },
                    onDragEnd = {
                        if (totalDragY > 70f) {
                            onSwipeDown()
                        }
                        totalDragY = 0f
                    },
                    onDragCancel = { totalDragY = 0f },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        totalDragY += dragAmount
                        if (totalDragY > 100f) {
                            onSwipeDown()
                            totalDragY = 0f
                        }
                    }
                )
            }
            .testTag("calculator_display")
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Interactive Swipe-Down Hint Bar at top of display
            Surface(
                onClick = onSwipeDown,
                color = (if (isDarkTheme) Color.White else Color.Black).copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 12.dp)
                    .testTag("history_pull_hint")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.swipe_down_hint),
                        tint = OrangeOperator,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.swipe_down_hint),
                        color = textColorExpression,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f, fill = false))

            // Current expression (small, gray, right-aligned)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(exprScrollState),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = if (expression.isBlank()) " " else expression,
                    color = textColorExpression,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.testTag("expression_text")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Result (large, white, bold, right-aligned)
            Text(
                text = resultText,
                color = textColorResult,
                fontSize = resultFontSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                maxLines = 1,
                lineHeight = (resultFontSize.value * 1.1f).sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("result_text")
            )
        }
    }
}
