package com.example.ollamaui.ui.screen.chat.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.theme.OllamaUITheme
import com.example.ollamaui.utils.Constants.TOP_BAR_HEIGHT

@Composable
fun ChatTopBar(
    modifier: Modifier = Modifier,
    @DrawableRes chatIcon: Int,
    botName: String,
    chatTitle: String,
    onBackClick: () -> Unit,
    onCopyClick: () -> Unit,
    isCopyButtonEnabled: Boolean
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
                .height(TOP_BAR_HEIGHT)
                .padding(start = 5.dp, end = 5.dp),
        ) {
            CustomButton(
                description = "Back Icon",
                onButtonClick = onBackClick,
                icon = R.drawable.baseline_arrow_back_24,
                buttonSize = 50,
                containerColor = MaterialTheme.colorScheme.background
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .clip(RoundedCornerShape(100))
                        .background(color = MaterialTheme.colorScheme.surfaceVariant)
                        .size(75.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(chatIcon),
                        contentDescription = chatTitle,
                        modifier = Modifier
                            .clip(RoundedCornerShape(100))
                            .background(color = MaterialTheme.colorScheme.outline)
                            .size(65.dp),
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                ChatTitle(title = chatTitle, botName = botName)
            }
            AnimatedVisibility(
                visible = isCopyButtonEnabled,
                enter = scaleIn(),
                exit = scaleOut(),
                ) {
                CustomButton(
                    description = "Copy Icon",
                    onButtonClick = onCopyClick,
                    icon = R.drawable.baseline_content_copy_24,
                    buttonSize = 50,
                    containerColor = MaterialTheme.colorScheme.background,
                    isButtonEnabled = isCopyButtonEnabled
                )
            }
        }
        HorizontalDivider()
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ChatTopBarPreview() {
    OllamaUITheme {
        ChatTopBar(
            botName = "Very very very very very long name",
            chatTitle = "Very very very very very long title",
            chatIcon = R.drawable.avatar_man_03,
            onBackClick = {},
            onCopyClick = {},
            isCopyButtonEnabled = false
        )
    }
}