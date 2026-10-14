package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.CalculatorDisplay
import com.example.ui.components.CalculatorKeypad
import com.example.ui.components.HistorySheet
import com.example.ui.components.SettingsDialog
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightTextDark
import com.example.ui.theme.LightTextGray
import com.example.ui.theme.OrangeOperator
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val historyList by viewModel.historyList.collectAsState()

    val isDark = settings.isDarkTheme
    val backgroundColor = if (isDark) DarkBackground else LightBackground
    val iconColor = if (isDark) TextGray else LightTextGray
    val titleColor = if (isDark) TextWhite else LightTextDark

    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Handle back button when sheets are open
    BackHandler(enabled = uiState.isHistoryOpen || uiState.isSettingsOpen) {
        if (uiState.isHistoryOpen) {
            viewModel.setHistoryOpen(false)
        } else if (uiState.isSettingsOpen) {
            viewModel.setSettingsOpen(false)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.TopCenter
    ) {
        // Center content and bound width for tablet/wide screens
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 500.dp)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Bar with branding, history quick-tap button, and settings button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    color = titleColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { viewModel.setHistoryOpen(true) },
                    modifier = Modifier.testTag("top_bar_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = stringResource(R.string.history_title),
                        tint = if (historyList.isNotEmpty()) OrangeOperator else iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = { viewModel.setSettingsOpen(true) },
                    modifier = Modifier.testTag("top_bar_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.settings_title),
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Screen 1: Calculator Home Display Area (large, right-aligned, swipe-down gesture)
            CalculatorDisplay(
                expression = uiState.expression,
                resultText = uiState.resultText,
                isDarkTheme = isDark,
                onSwipeDown = {
                    viewModel.setHistoryOpen(true)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            // Screen 1: 4-Column Keypad Below
            CalculatorKeypad(
                isDarkTheme = isDark,
                isHapticEnabled = settings.isHapticFeedbackEnabled,
                onDigitClick = { digit -> viewModel.onDigit(digit) },
                onOperatorClick = { op -> viewModel.onOperator(op) },
                onDotClick = { viewModel.onDot() },
                onEqualsClick = { viewModel.onEquals() },
                onClearClick = { viewModel.onClear() },
                onToggleSignClick = { viewModel.onToggleSign() },
                onPercentageClick = { viewModel.onPercentage() },
                onBackspaceClick = { viewModel.onBackspace() },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Screen 2: History Panel (slide-up sheet)
        if (uiState.isHistoryOpen) {
            HistorySheet(
                historyList = historyList,
                isDarkTheme = isDark,
                sheetState = historySheetState,
                onDismiss = {
                    scope.launch {
                        historySheetState.hide()
                        viewModel.setHistoryOpen(false)
                    }
                },
                onReuseItem = { item, loadAsExpression ->
                    scope.launch {
                        historySheetState.hide()
                        viewModel.reuseHistoryItem(item, loadAsExpression)
                    }
                },
                onClearHistory = {
                    viewModel.clearHistory()
                }
            )
        }

        // Screen 3: Settings Panel (slide-up sheet)
        if (uiState.isSettingsOpen) {
            SettingsDialog(
                settings = settings,
                onSettingsChanged = { updated ->
                    viewModel.updateSettings(updated)
                },
                sheetState = settingsSheetState,
                onDismiss = {
                    scope.launch {
                        settingsSheetState.hide()
                        viewModel.setSettingsOpen(false)
                    }
                }
            )
        }
    }
}
