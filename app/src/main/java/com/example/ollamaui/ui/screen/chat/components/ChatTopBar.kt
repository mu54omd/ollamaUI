package com.example.ollamaui.ui.screen.chat.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.screen.home.components.LogoTitle
import com.example.ollamaui.ui.theme.OllamaUITheme
import com.example.ollamaui.utils.Constants.TOP_BAR_HEIGHT

@Composable
fun ChatTopBar(
    modifier: Modifier = Modifier,
    @DrawableRes chatIcon: Int,
    chatTitle: String,
    onBackClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .height(TOP_BAR_HEIGHT)
                .padding(start = 5.dp, end = 5.dp),
        ) {
            CustomButton(
                description = "Back Icon",
                onButtonClick = onBackClick,
                icon = R.drawable.baseline_arrow_back_24,
                buttonSize = 50,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
            Spacer(modifier = Modifier.width(20.dp))
            Image(
                painter = painterResource(chatIcon),
                contentDescription = "Chat Icon",
                modifier = Modifier.size(50.dp)
                )
            Spacer(modifier = Modifier.width(20.dp))
            LogoTitle(
                text = chatTitle
            )
        }
        HorizontalDivider()
    }
}

@Preview
@Composable
private fun ChatTopBarPreview() {
    OllamaUITheme {
        ChatTopBar(
            chatTitle = "Title",
            chatIcon = R.drawable.avatar_logo_01,
            onBackClick = {}
        )
    }
}