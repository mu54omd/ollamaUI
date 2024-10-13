package com.example.ollamaui.ui.screen.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton

@Composable
fun CustomFabButton(
    modifier: Modifier = Modifier,
    isModelListLoaded: Boolean = false,
    isNewChatDialogVisible: Boolean,
    onButtonClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
    ) {
        Spacer(Modifier.height(5.dp))
        AnimatedVisibility(
            visible = !isNewChatDialogVisible
        ) {
            Crossfade(
                targetState = isModelListLoaded,
                label = "Fab Cross fade"
            ) { isModelListLoaded ->
                when (isModelListLoaded) {
                    true -> CustomButton(
                        description = "New Chat",
                        onButtonClick = onButtonClick,
                        icon = R.drawable.baseline_add_24,
                        iconSize = 40,
                        buttonSize = 60,
                    )

                    false -> CustomButton(
                        description = "New Chat",
                        onButtonClick = onButtonClick,
                        icon = R.drawable.baseline_refresh_24,
                        iconSize = 40,
                        buttonSize = 60,
                    )
                }
            }
        }
    }
}