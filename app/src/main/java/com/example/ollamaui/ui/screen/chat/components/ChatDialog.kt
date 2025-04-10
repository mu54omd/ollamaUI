package com.example.ollamaui.ui.screen.chat.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.ui.common.filterAssistantMessage
import com.example.ollamaui.ui.common.filterUserMessage
import com.example.ollamaui.ui.common.isFromMe
import com.example.ollamaui.ui.theme.OllamaUITheme
import com.example.ollamaui.utils.Constants.USER_ROLE
import com.halilibo.richtext.commonmark.CommonmarkAstNodeParser
import com.halilibo.richtext.markdown.BasicMarkdown
import com.halilibo.richtext.ui.CodeBlockStyle
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.material3.RichText
import com.halilibo.richtext.ui.string.RichTextStringStyle

@Composable
fun ChatDialog(
    messageModel: MessageModel,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
    onSelectedItemClick: () -> Unit,
    onLongPressItem: () -> Unit,
    isSelected: Boolean,
    isDetailsVisible: Boolean,
    isSendingFailed: Boolean,
    isLastMinusOneMessage: Boolean,
    isLastMessage: Boolean,
    onReproduceResponse: () -> Unit,
    onDeleteLastMessage: () -> Unit,
    onEditLastMessage: () -> Unit,
    isResponding: Boolean
) {
    val isFromMe by remember { derivedStateOf { isFromMe(messageModel) } }
    val animatedColorMessage by animateColorAsState(
        when(isFromMe){
            true -> {
                if(isSelected) MaterialTheme.colorScheme.tertiaryContainer
                else if(isSendingFailed && isLastMessage) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.primaryContainer

            }
            false -> {
                if(isSelected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.background
            }
        },
        label = "Animated Color Message",
    )
    val textBgColor = if(!isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
    val richTextStyle = RichTextStyle(
        codeBlockStyle = CodeBlockStyle(
            textStyle = MaterialTheme.typography.bodySmall,
            padding = 6.sp,
            modifier = Modifier.background(
                color = textBgColor)
        ),
        stringStyle = RichTextStringStyle(
            codeStyle = SpanStyle(
                background = textBgColor,
                fontStyle = FontStyle.Italic,
                fontSize = 14.sp
            )
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, bottom = 10.dp)
    ) {
            Box(
                modifier = Modifier
                    .align(if (isFromMe) Alignment.End else Alignment.Start)
                    .widthIn(min = 120.dp)
                    .border(
                        width = (1.5).dp,
                        color = when{
                            isFromMe -> MaterialTheme.colorScheme.surface
                            else -> MaterialTheme.colorScheme.outlineVariant
                        },
                        shape =
                            RoundedCornerShape(
                                topStart = if (isFromMe) 50f else 0f,
                                topEnd = if (isFromMe) 0f else 50f,
                                bottomStart = 50f,
                                bottomEnd = 50f
                            )
                    )
                    .clip(
                        RoundedCornerShape(
                            topStart = if (isFromMe) 50f else 0f,
                            topEnd = if (isFromMe) 0f else 50f,
                            bottomStart = 50f,
                            bottomEnd = 50f
                        )
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                when {
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
                    .drawBehind {
                        drawRoundRect(
                            color = animatedColorMessage,
                        )
                    }
                    .padding(16.dp)

            ) {
                Column(
                    horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start,
                ) {
                    if(messageModel.role == USER_ROLE) {
                        Text(text = filterUserMessage(messageModel.content)?:messageModel.content )
                    }else{
                        val thinkingText = filterAssistantMessage(assistantMessage = messageModel.content)
                        if(thinkingText.first != null){
                            RichText(
                                style = richTextStyle
                            ) {
                                val parser = remember { CommonmarkAstNodeParser() }
                                val astNode = remember(parser) {
                                    parser.parse(messageModel.content)
                                }
                                BasicMarkdown(astNode)
                            }
                        }else{
                            if(thinkingText.second?.isNotEmpty() == true) {
                                Text(
                                    text = thinkingText.second!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontStyle = FontStyle.Italic,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .padding(bottom = 4.dp)
                                        .drawBehind {
                                            val strokeWidth = 1.dp.toPx()
                                            drawLine(
                                                color = Color.LightGray,
                                                start = Offset(0f, size.height),
                                                end = Offset(size.width, size.height),
                                                strokeWidth = strokeWidth
                                            )
                                        }
                                        .padding(top = 5.dp, bottom = 5.dp)
                                )
                            }
                            RichText(
                                style = richTextStyle
                            ) {
                                val parser = remember { CommonmarkAstNodeParser() }
                                val astNode = remember(parser) {
                                    parser.parse(thinkingText.third!!)
                                }
                                BasicMarkdown(astNode)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
        ChatDialogDetails(
            isVisible = isDetailsVisible,
            isFromMe = isFromMe,
            date = messageModel.date,
            time = messageModel.time,
            isLastBotMessage = isLastMessage && !isFromMe,
            isLastUserMessage = (isLastMessage || isLastMinusOneMessage ) && isFromMe,
            onReproduceResponse = onReproduceResponse,
            onEditLastMessage = onEditLastMessage,
            onDeleteLastMessage = onDeleteLastMessage,
            isResponding = isResponding
        )
    }

}
@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ChatDialogPreview() {
    OllamaUITheme {
        Column {
            ChatDialog(
                messageModel = MessageModel(
                    content = "<think> User ask some question and we should answer that</think> Hi. How are you???",
                    role = "system",
                    time = "17:43",
                    date = "2025-05-17",
                ),
                isSelected = false,
                isDetailsVisible = false,
                isSendingFailed = false,
                onItemClick = {},
                onSelectedItemClick = {},
                onLongPressItem = {},
                isLastMessage = true,
                isLastMinusOneMessage = false,
                onReproduceResponse = {},
                onEditLastMessage = {},
                onDeleteLastMessage = {},
                isResponding = false
            )
            ChatDialog(
                messageModel = MessageModel(
                    content = "<think>\n\n</think> Hi. How are you???",
                    role = "assistant",
                    time = "17:43",
                    date = "2025-05-17",
                ),
                isSelected = false,
                isDetailsVisible = false,
                isSendingFailed = false,
                onItemClick = {},
                onSelectedItemClick = {},
                onLongPressItem = {},
                isLastMessage = true,
                isLastMinusOneMessage = false,
                onReproduceResponse = {},
                onEditLastMessage = {},
                onDeleteLastMessage = {},
                isResponding = false
            )
            ChatDialog(
                messageModel = MessageModel(
                    content = "Let's start with the basic syntax of Kotlin:\n\n**Variables**\n\nIn Kotlin, variables are declared using the `var` keyword. We can declare a variable and assign it a value in one line like this:\n```\nval name = \"Musa\"\nprintln(name)\n```\nHere, `name` is a local variable (declared inside the function), which is stored on the stack.\n\n",
                    role = "assistant",
                    time = "17:43",
                    date = "2025-05-17",
                ),
                isSelected = false,
                isDetailsVisible = false,
                isSendingFailed = false,
                onItemClick = {},
                onSelectedItemClick = {},
                onLongPressItem = {},
                isLastMinusOneMessage = true,
                isLastMessage = false,
                onReproduceResponse = {},
                onEditLastMessage = {},
                onDeleteLastMessage = {},
                isResponding = false
            )
            ChatDialog(
                messageModel = MessageModel(
                    content = "Using this data: {/n/n/n/some data}. Respond to this prompt: {some prompt}.",
                    role = "user",
                    time = "17:43",
                    date = "2025-05-17",
                ),
                isSelected = false,
                isDetailsVisible = false,
                isSendingFailed = false,
                onItemClick = {},
                onSelectedItemClick = {},
                onLongPressItem = {},
                isLastMinusOneMessage = true,
                isLastMessage = false,
                onReproduceResponse = {},
                onEditLastMessage = {},
                onDeleteLastMessage = {},
                isResponding = false
            )
        }
    }
}