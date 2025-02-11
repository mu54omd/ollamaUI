package com.example.ollamaui.ui.screen.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.screen.common.CustomDropDownList
import com.example.ollamaui.ui.theme.OllamaUITheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatModal(
    systemPrompt: String,
    onSystemPromptChange: (String) -> Unit,
    chatTitle: String,
    onChatTitleChange: (String) -> Unit,
    onCloseClick: () -> Unit,
    onAcceptClick: () -> Unit,
    maxChar: Int,
    modelList: List<String>,
    onModelClick: (String) -> Unit,
    ) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet (
        onDismissRequest = { onCloseClick() },
        containerColor = MaterialTheme.colorScheme.inversePrimary,
        sheetState = bottomSheetState
    ) {
        var isModelSelected by remember { mutableStateOf(false) }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
            ) {
            val focusManager = LocalFocusManager.current
            CustomDropDownList(
                listItems = modelList,
                onItemClick = {
                    onModelClick(it)
                    isModelSelected = true
                },
                modifier = Modifier.padding(bottom = 20.dp)
            )
            CustomTextField(
                value = chatTitle,
                onValueChange = onChatTitleChange,
                label = "Chat Title",
                maxChar = maxChar,
                onDone = { focusManager.moveFocus(FocusDirection.Down) },
            )
            Spacer(modifier = Modifier.height(2.dp))
            Spacer(modifier = Modifier.height(2.dp))
            CustomTextField(
                value = systemPrompt,
                onValueChange = onSystemPromptChange,
                label = "Describe the bot",
                maxChar = maxChar * 6,
                onDone = { if (chatTitle.isNotEmpty() && isModelSelected) onAcceptClick() },
                maxLines = 5,
                minLines = 5,
                roundCornerPercent = 10
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
            ) {
                CustomButton(
                    description = "Accept",
                    onButtonClick = onAcceptClick,
                    icon = R.drawable.baseline_check_24,
                    buttonSize = 50,
                    isButtonEnabled = chatTitle.isNotEmpty() && isModelSelected,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
                Spacer(modifier = Modifier.width(50.dp))
                CustomButton(
                    description = "Close",
                    onButtonClick = onCloseClick,
                    icon = R.drawable.baseline_clear_24,
                    buttonSize = 50,
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun NewChatModalPreview() {
    OllamaUITheme {
        NewChatModal(
            chatTitle = "Title",
            systemPrompt = "You are a serial killer!",
            maxChar = 50,
            onChatTitleChange = {},
            onCloseClick = {},
            onAcceptClick = {},
            onSystemPromptChange = {},
            modelList = listOf("model 1", "model 2"),
            onModelClick = {},
        )
    }
}