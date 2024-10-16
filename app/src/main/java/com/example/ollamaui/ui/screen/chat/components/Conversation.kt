package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ollamaui.R
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.domain.model.MessagesModel
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.utils.Constants.SYSTEM_ROLE

@Composable
fun Conversation(
    botName: String,
    userName: String,
    messagesModel: MessagesModel,
    modifier: Modifier = Modifier,
    isSelected: (Int, MessageModel) -> Boolean,
    isVisible: (Int, MessageModel) -> Boolean,
    onItemClick: (Int, MessageModel) -> Unit,
    onLongPressItem: (Int, MessageModel) -> Unit,
    onSelectedItemClick: (Int, MessageModel) -> Unit,
    listState: LazyListState,
) {
    LazyColumn(
        modifier = modifier.padding(bottom = 10.dp),
        verticalArrangement = Arrangement.Bottom,
        state = listState,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp)
    ) {
        itemsIndexed(
            items = messagesModel.messageModels,
            key = { index, _ ->  index}
        ){ index, message ->
            if (message.role != SYSTEM_ROLE) {
                ChatDialog(
                    messageModel = message,
                    modifier = Modifier.animateItem(),
                    botName = botName,
                    userName = userName,
                    isSelected = isSelected(index, message),
                    isVisible = isVisible(index, message),
                    onLongPressItem = { onLongPressItem(index, message) },
                    onItemClick = { onItemClick(index, message) },
                    onSelectedItemClick = { onSelectedItemClick(index, message) },
                )
            }
        }
    }
}