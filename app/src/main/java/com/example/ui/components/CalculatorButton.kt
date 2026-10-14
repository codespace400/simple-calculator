package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkFunctionButton
import com.example.ui.theme.DarkNumberButton
import com.example.ui.theme.LightFunctionButton
import com.example.ui.theme.LightNumberButton
import com.example.ui.theme.LightTextDark
import com.example.ui.theme.OrangeOperator
import com.example.ui.theme.TextWhite

enum class ButtonType {
    NUMBER,
    OPERATOR,
    FUNCTION
}

@Composable
fun CalculatorButton(
    text: String,
    type: ButtonType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = true,
    isHapticEnabled: Boolean = true,
    contentDescription: String? = null,
    testTag: String = "btn_$text"
) {
    val haptic: HapticFeedback = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth subtle bounce scale effect on press for tactile feedback
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "button_scale"
    )

    // Button Shape: Circular for operators, Rounded Square (20.dp) for numbers & functions
    val shape: Shape = when (type) {
        ButtonType.OPERATOR -> CircleShape
        ButtonType.NUMBER -> RoundedCornerShape(20.dp)
        ButtonType.FUNCTION -> RoundedCornerShape(20.dp)
    }

    // Colors matching specification
    val backgroundColor: Color = when (type) {
        ButtonType.OPERATOR -> OrangeOperator
        ButtonType.NUMBER -> if (isDarkTheme) DarkNumberButton else LightNumberButton
        ButtonType.FUNCTION -> if (isDarkTheme) DarkFunctionButton else LightFunctionButton
    }

    val textColor: Color = when (type) {
        ButtonType.OPERATOR -> TextWhite
        ButtonType.NUMBER -> if (isDarkTheme) TextWhite else LightTextDark
        ButtonType.FUNCTION -> if (isDarkTheme) TextWhite else LightTextDark
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .sizeIn(minWidth = 56.dp, minHeight = 56.dp)
            .scale(scale)
            .clip(shape)
            .background(backgroundColor, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(color = Color.White.copy(alpha = 0.35f)),
                role = Role.Button,
                onClick = {
                    if (isHapticEnabled) {
                        try {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        } catch (_: Exception) {}
                    }
                    onClick()
                }
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = if (text.length > 2) 22.sp else 28.sp,
            fontWeight = if (type == ButtonType.OPERATOR) FontWeight.Bold else FontWeight.Medium,
            style = MaterialTheme.typography.titleLarge
        )
    }
}
