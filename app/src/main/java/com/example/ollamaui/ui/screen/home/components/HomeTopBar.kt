package com.example.ollamaui.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.theme.OllamaUITheme
import com.example.ollamaui.utils.Constants.TOP_BAR_HEIGHT

@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    onSettingClick: () -> Unit,
    onAboutClick: () -> Unit,
) {
    Column {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .height(TOP_BAR_HEIGHT)
                .padding(start = 5.dp, end = 5.dp),
            ) {
            CustomButton(
                description = "Setting Button",
                onButtonClick = onSettingClick,
                icon = R.drawable.baseline_settings_24,
                buttonSize = 50,
                modifier = Modifier.align(Alignment.CenterStart),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
            LogoTitle(
                text = "Ollama UI",
                modifier = Modifier.align(Alignment.Center)
            )
            CustomButton(
                description = "About Button",
                onButtonClick = onAboutClick,
                icon = R.drawable.baseline_info_outline_24,
                buttonSize = 50,
                modifier = Modifier.align(Alignment.CenterEnd),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
        HorizontalDivider()
    }
}

@Preview
@Composable
private fun HomeTopBarPreview() {
    OllamaUITheme {
        HomeTopBar(
            onAboutClick = {},
            onSettingClick = {}
        )
    }
}