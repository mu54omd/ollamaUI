package com.example.ollamaui.ui.screen.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.ui.theme.OllamaUITheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropDownList(
    modifier: Modifier = Modifier,
    defaultValue: String = "",
    label: String = "",
    width: Int = 200,
    listItems: List<String>,
    onItemClick: (String) -> Unit,
    isEnable: Boolean = true
) {
    var isExpanded by remember{ mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(defaultValue) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(top = 5.dp, bottom = 5.dp)
    ){
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded}
        ) {
            TextField(
                value = selectedItem,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { if(isEnable) ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                placeholder = { Text(text = label)},
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                    .width(width.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = MaterialTheme.shapes.medium
                    ),
                textStyle = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
                shape = MaterialTheme.shapes.large,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                ),
                enabled = isEnable
            )
            if (isEnable) {
                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.height(100.dp),
                    containerColor = MaterialTheme.colorScheme.background,
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                ) {

                    listItems.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = item,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            },
                            onClick = {
                                selectedItem = item
                                isExpanded = false
                                onItemClick(item)
                            },
                        )
                        if (index != listItems.lastIndex)
                            HorizontalDivider(
                                modifier = Modifier
                                    .width((width - 30).dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DropDownListPreview() {
    OllamaUITheme {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()) {
            CustomDropDownList(
                defaultValue = "a",
                listItems = listOf("a", "b", "c", "d", "b", "c", "d", "b", "c", "d"),
                onItemClick = {},
                isEnable = true
            )
        }
    }
}