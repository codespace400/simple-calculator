package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CalculatorSettings
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceVariant
import com.example.ui.theme.LightTextDark
import com.example.ui.theme.LightTextGray
import com.example.ui.theme.OrangeOperator
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsDialog(
    settings: CalculatorSettings,
    onSettingsChanged: (CalculatorSettings) -> Unit,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = settings.isDarkTheme
    val sheetBg = if (isDark) DarkSurface else LightSurface
    val textPrimary = if (isDark) TextWhite else LightTextDark
    val textSecondary = if (isDark) TextGray else LightTextGray
    val sectionBg = if (isDark) DarkSurfaceVariant.copy(alpha = 0.5f) else LightSurfaceVariant.copy(alpha = 0.5f)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = sheetBg,
        tonalElevation = 6.dp,
        modifier = modifier.testTag("settings_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(OrangeOperator.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = OrangeOperator,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.settings_title),
                        color = textPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_settings_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Theme Toggle
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = sectionBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("setting_theme_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (settings.isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = OrangeOperator,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.dark_mode),
                                color = textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (settings.isDarkTheme) "Dark (#111827 default)" else "Light Theme",
                                color = textSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Switch(
                        checked = settings.isDarkTheme,
                        onCheckedChange = { isChecked ->
                            onSettingsChanged(settings.copy(isDarkTheme = isChecked))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = OrangeOperator,
                            uncheckedThumbColor = textSecondary,
                            uncheckedTrackColor = sectionBg
                        ),
                        modifier = Modifier.testTag("theme_toggle_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Haptic Feedback Toggle
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = sectionBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("setting_haptic_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = null,
                            tint = OrangeOperator,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.haptic_feedback),
                                color = textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = stringResource(R.string.haptic_feedback_desc),
                                color = textSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Switch(
                        checked = settings.isHapticFeedbackEnabled,
                        onCheckedChange = { isChecked ->
                            onSettingsChanged(settings.copy(isHapticFeedbackEnabled = isChecked))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = OrangeOperator,
                            uncheckedThumbColor = textSecondary,
                            uncheckedTrackColor = sectionBg
                        ),
                        modifier = Modifier.testTag("haptic_toggle_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Decimal Precision Selector
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = sectionBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("setting_precision_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.decimal_precision),
                        color = textPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(R.string.decimal_precision_desc),
                        color = textSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val precisionOptions = listOf(
                        -1 to "Auto",
                        2 to "2 (0.00)",
                        4 to "4 (0.0000)",
                        6 to "6",
                        8 to "8"
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        precisionOptions.forEach { (precision, label) ->
                            val isSelected = settings.decimalPrecision == precision
                            val chipBg = if (isSelected) OrangeOperator else (if (isDark) DarkSurface else LightSurface)
                            val chipTextColor = if (isSelected) TextWhite else textPrimary

                            Surface(
                                onClick = {
                                    onSettingsChanged(settings.copy(decimalPrecision = precision))
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = chipBg,
                                tonalElevation = if (isSelected) 4.dp else 1.dp,
                                modifier = Modifier.testTag("precision_option_$precision")
                            ) {
                                Text(
                                    text = label,
                                    color = chipTextColor,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
