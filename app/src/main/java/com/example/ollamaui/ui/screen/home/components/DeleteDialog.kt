package com.example.ollamaui.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ollamaui.R
import com.example.ollamaui.ui.screen.common.CustomButton
import com.example.ollamaui.ui.theme.OllamaUITheme

@Composable
fun DeleteDialog(
    modifier: Modifier = Modifier,
    chatTitle: String,
    userName: String,
    onCloseClick: () -> Unit,
    onAcceptClick: () -> Unit
) {
    Dialog(
        onDismissRequest = { onCloseClick() }
    ) {
        Box(
            modifier = modifier
                .clip(shape = MaterialTheme.shapes.large)
                .size(200.dp, 175.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ){
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(100.dp))
                    .background(color = MaterialTheme.colorScheme.errorContainer)
                    .align(alignment = BiasAlignment(0f, -0.85f))
                    .padding(4.dp)
                ,
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Delete confirmation",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(100.dp)
            ) {
                Text(text = "Chat title" , style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = chatTitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, overflow = TextOverflow.Ellipsis, maxLines = 1)
                Text(text = "Author", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = userName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, overflow = TextOverflow.Ellipsis, maxLines = 1)
            }
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
            ) {
                CustomButton(
                    description = "Accept",
                    onButtonClick = onAcceptClick,
                    icon = R.drawable.baseline_check_24,
                    buttonSize = 50,
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
                Spacer(modifier = Modifier.width(30.dp))
                CustomButton(
                    description = "Close",
                    onButtonClick = onCloseClick,
                    icon = R.drawable.baseline_clear_24,
                    buttonSize = 50,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteDialogPreview() {
    OllamaUITheme {
        DeleteDialog(
            chatTitle = "newChat",
            userName = "author",
            onCloseClick = {},
            onAcceptClick = {},
        )
    }
}