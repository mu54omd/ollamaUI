package com.example.ollamaui.ui.screen.chat.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.ui.common.isFromMe
import com.example.ollamaui.ui.theme.OllamaUITheme
import com.example.ollamaui.utils.Constants.USER_ROLE
import com.halilibo.richtext.commonmark.CommonmarkAstNodeParser
import com.halilibo.richtext.markdown.BasicMarkdown
import com.halilibo.richtext.ui.material3.RichText

@Composable
fun ChatDialog(
    messageModel: MessageModel,
    userName: String,
    botName: String,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
    onSelectedItemClick: () -> Unit,
    onLongPressItem: () -> Unit,
    isSelected: Boolean,
    isVisible: Boolean,
) {
    val isFromMe = isFromMe(messageModel)
    val animatedColorMyMessage by animateColorAsState(
        if(isSelected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer,
        label = "Animated Color My Message",
    )
    val animatedColorBotMessage by animateColorAsState(
        if(isSelected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer,
        label = "Animated Color Bot Message",
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
            Box(
                modifier = Modifier
                    .align(if (isFromMe(messageModel)) Alignment.End else Alignment.Start)
                    .clip(
                        RoundedCornerShape(
                            topStart = 48f,
                            topEnd = 48f,
                            bottomStart = if (isFromMe) 48f else 0f,
                            bottomEnd = if (isFromMe) 0f else 48f
                        )
                    )
                    .pointerInput(Unit){
                        detectTapGestures(
                            onTap = {
                                when{
                                    !isSelected -> {
                                        onItemClick()
                                    }
                                    isSelected -> {
                                        onSelectedItemClick()
                                    }
                                }
                            },
                            onLongPress = {
                                onLongPressItem()
                            }
                        )
                    }
                    .background(color = if (isFromMe) animatedColorMyMessage else animatedColorBotMessage)
                    .padding(16.dp)

            ) {
                Column(
                    horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start,
                ) {
                    if(messageModel.role == USER_ROLE) {
                        Text(
                            text = if (messageModel.content.split(" Respond to this prompt: ").size == 1) {
                                messageModel.content.trim()
                            } else {
                                messageModel.content.split(" Respond to this prompt: ")[1].substring(
                                    1,
                                ).dropLast(2).trim()
                            }
                        )
                    }else{
                        val thinking = messageModel.content.split("</think>")
                        if(thinking.size == 1){
                            RichText {
                                val parser = remember { CommonmarkAstNodeParser() }
                                val astNode = remember(parser) {
                                    parser.parse(messageModel.content)
                                }
                                BasicMarkdown(astNode)
                            }
                        }else{
                            Text(
                                text = thinking[0].substring(8).trim(),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .padding(bottom = 4.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .border(width = 1.dp, color = Color.Gray, shape = MaterialTheme.shapes.small)
                                    .padding(10.dp)
                                )
                            RichText {
                                val parser = remember { CommonmarkAstNodeParser() }
                                val astNode = remember(parser) {
                                    parser.parse(thinking[1].trim())
                                }
                                BasicMarkdown(astNode)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
        ChatDialogDetails(isVisible = isVisible, isFromMe = isFromMe, userName = userName, botName = botName)
    }

}

@Preview
@Composable
private fun ChatDialogPreview() {
    OllamaUITheme {
        Column {
            ChatDialog(
                messageModel = MessageModel(
                    content = "<think> User ask some question </think> Hi. How are you???",
                    role = "system",
                ),
                userName = "Musa",
                botName = "Musashi",
                isSelected = false,
                isVisible = false,
                onItemClick = {},
                onSelectedItemClick = {},
                onLongPressItem = {},
            )
            ChatDialog(
                messageModel = MessageModel(
                    content = "Hello! I'm fine.",
                    role = "assistant"
                ),
                userName = "Musa",
                botName = "Musashi",
                isSelected = false,
                isVisible = false,
                onItemClick = {},
                onSelectedItemClick = {},
                onLongPressItem = {},
            )
            ChatDialog(
                messageModel = MessageModel(
                    content = "Let's start with the basic syntax of Kotlin:\n\n**Variables**\n\nIn Kotlin, variables are declared using the `var` keyword. We can declare a variable and assign it a value in one line like this:\n```\nval name = \"Musa\"\nprintln(name)\n```\nHere, `name` is a local variable (declared inside the function), which is stored on the stack.\n\n",
                    role = "assistant"
                ),
                userName = "Musa",
                botName = "Musashi",
                isSelected = false,
                isVisible = false,
                onItemClick = {},
                onSelectedItemClick = {},
                onLongPressItem = {},
            )
            ChatDialog(
                messageModel = MessageModel(
                    content = "Using this data: {some data}. Respond to this prompt: {some prompt}.",
                    role = "user"
                ),
                userName = "Musa",
                botName = "Musashi",
                isSelected = false,
                isVisible = false,
                onItemClick = {},
                onSelectedItemClick = {},
                onLongPressItem = {},
            )
        }
    }
}