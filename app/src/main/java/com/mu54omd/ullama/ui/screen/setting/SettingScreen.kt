package com.mu54omd.ullama.ui.screen.setting

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mu54omd.ullama.domain.model.chat.DefaultModelParameters
import com.mu54omd.ullama.domain.model.chat.ModelParameters
import com.mu54omd.ullama.ui.screen.common.CustomDropDownList
import com.mu54omd.ullama.ui.screen.setting.components.CustomSettingBox
import com.mu54omd.ullama.ui.screen.setting.components.TuningSlider
import com.mu54omd.ullama.ui.theme.ULlamaTheme
import com.mu54omd.ullama.utils.Constants.OLLAMA_IS_RUNNING
import kotlinx.coroutines.launch

@Composable
fun SettingScreen(
    savedParameters: List<String>,
    ollamaStatus: String,
    embeddingModelList: List<String>,
    isEmbeddingModelPulled: (String) -> Boolean,
    onSaveClick: (url: String,embeddingModel: String, tuningParameters: ModelParameters) -> Unit,
    onCheckClick: (url: String) -> Unit,
    onPullEmbeddingModelClick: (String) -> Unit,
    onFetchEmbeddingModelClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val ipRegex = remember { Regex("^(\\d{0,3}\\.){0,3}\\d{0,3}$") }
    val portRegex = Regex("\\d{0,5}")
    var ipAddress by rememberSaveable { mutableStateOf(savedParameters[0].split("//")[1].split(":")[0]) }
    var port by rememberSaveable { mutableStateOf(savedParameters[0].split("//")[1].split(":")[1]) }
    var selectedEmbeddingModel by rememberSaveable { mutableStateOf(if(savedParameters[1].isEmpty()) embeddingModelList[0] else savedParameters[1]) }
    var isSelectedModelPulled = remember { derivedStateOf { isEmbeddingModelPulled(selectedEmbeddingModel) } }
    val ipOnValueChange = remember {
        { input: String ->
            if (ipRegex.matches(input)) {
                ipAddress = input
            }
        }
    }
    val portOnValueChange = remember {
        { input: String ->
            if (portRegex.matches(input)) {
                port = input
            }
        }
    }
    val sliderPositions = remember {
        mutableStateListOf(
            savedParameters[2].toFloat(),
            savedParameters[3].toFloat(),
            savedParameters[4].toFloat(),
            savedParameters[5].toFloat(),
            savedParameters[6].toFloat(),
            savedParameters[7].toFloat(),
            savedParameters[8].toFloat()
        )
    }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val statusColor by animateColorAsState(
        when(ollamaStatus) {
            OLLAMA_IS_RUNNING -> MaterialTheme.colorScheme.tertiaryContainer
            "" -> MaterialTheme.colorScheme.background
            else -> MaterialTheme.colorScheme.errorContainer
        }
    )
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState){ snackbarData ->
                Snackbar(
                    shape = RoundedCornerShape(100),
                    modifier = Modifier.fillMaxWidth(0.5f)
                ){
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = snackbarData.visuals.message, modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        },
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues = padding)
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
        ) {
            CustomSettingBox(
                title = "Ollama Address",
            ) {
                OutlinedTextField(
                    value = ipAddress,
                    onValueChange = ipOnValueChange,
                    label = { Text(text = "Ip address") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                    ),
                    singleLine = true,
                    supportingText = {
                        Text(text = ollamaStatus, modifier = Modifier.drawBehind{drawRoundRect(color = statusColor)}.padding(2.dp))
                    },
                    modifier = Modifier
                        .weight(0.3f)
                        .padding(start = 10.dp, top = 20.dp, bottom = 20.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))
                OutlinedTextField(
                    value = port,
                    onValueChange = portOnValueChange,
                    label = { Text(text = "Port") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .weight(0.2f)
                        .padding(top = 20.dp, end = 10.dp, bottom = 20.dp)
                )
                Button(
                    onClick = {
                        onCheckClick("http://$ipAddress:$port")
                    },
                    modifier = Modifier.padding(top = 30.dp, end = 10.dp)
                ) {
                    Text("Check")
                }
            }
            CustomSettingBox(
                title = "Embedding Model",
                rowHorizontalArrangement = Arrangement.Start
            ) {
                CustomDropDownList(
                    listItems = embeddingModelList,
                    onItemClick = {
                        selectedEmbeddingModel = it
                    },
                    defaultValue = selectedEmbeddingModel,
                    modifier = Modifier.padding(start = 10.dp, top = 20.dp, bottom = 5.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 40.dp, end = 10.dp)
                ) {
                    AnimatedVisibility(
                        visible = selectedEmbeddingModel != "Select a Model",
                        enter = scaleIn(),
                        exit = scaleOut()
                    ) {
                        TextButton(
                            onClick = { onPullEmbeddingModelClick(selectedEmbeddingModel) },
                            border = BorderStroke(
                                color = if(isSelectedModelPulled.value) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer,
                                width = 2.dp
                            )
                        ) {
                            Text(text = if(isSelectedModelPulled.value) "Pulled" else "Pull")
                        }
                    }
                    TextButton(
                        onClick = { onFetchEmbeddingModelClick },
                        border = BorderStroke(color = MaterialTheme.colorScheme.primaryContainer, width = 2.dp)
                    ) {
                        Text("Re-fetch")
                    }
                }
            }
            CustomSettingBox(
                title = "Tuning Parameters",
            ) {
                Column(
                    modifier = Modifier.padding(top = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    TuningSlider(
                        title = "Temperature",
                        sliderPosition = sliderPositions[0],
                        onSliderChange = { sliderPositions[0] = it },
                        explanation = "Regulates the unpredictability of output.",
                        isInteger = false,
                        startPosition = 0f,
                        endPosition = 2f,
                    )
                    TuningSlider(
                        title = "Context size",
                        sliderPosition = sliderPositions[1],
                        onSliderChange = { sliderPositions[1] = it },
                        explanation = "Sets the size of the context window\nused to generate the next token.",
                        isInteger = true,
                        startPosition = 1f,
                        endPosition = 8192f,
                    )
                    TuningSlider(
                        title = "Frequency penalty",
                        sliderPosition = sliderPositions[2],
                        onSliderChange = { sliderPositions[2] = it },
                        explanation = "Discourages repetition proportionally\nto how frequently they appear.",
                        isInteger = false,
                        startPosition = 0f,
                        endPosition = 2f
                    )
                    TuningSlider(
                        title = "Presence penalty",
                        sliderPosition = sliderPositions[3],
                        onSliderChange = { sliderPositions[3] = it },
                        explanation = "Discourages repetition based on\nif they have occurred or not.",
                        isInteger = false,
                        startPosition = 0f,
                        endPosition = 2f
                    )
                    TuningSlider(
                        title = "Top K",
                        sliderPosition = sliderPositions[4],
                        onSliderChange = { sliderPositions[4] = it },
                        explanation = "Reduces the probability of generating nonsense.\nHigher value will give more diverse answers",
                        isInteger = true,
                        startPosition = 0f,
                        endPosition = 100f,
                    )
                    TuningSlider(
                        title = "Top P ",
                        sliderPosition = sliderPositions[5],
                        onSliderChange = { sliderPositions[5] = it },
                        explanation = "Manage the randomness of their output.\nHigher value will lead to more diverse text.",
                        isInteger = false,
                        startPosition = 0f,
                        endPosition = 2f,
                    )
                    TuningSlider(
                        title = "Min P",
                        sliderPosition = sliderPositions[6],
                        onSliderChange = { sliderPositions[6] = it },
                        explanation = "Aims to ensure a balance of quality and variety.",
                        isInteger = false,
                        startPosition = 0f,
                        endPosition = 2f,
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        sliderPositions[0] = DefaultModelParameters.default.temperature
                        sliderPositions[1] = DefaultModelParameters.default.numCtx.toFloat()
                        sliderPositions[2] = DefaultModelParameters.default.presencePenalty
                        sliderPositions[3] = DefaultModelParameters.default.frequencyPenalty
                        sliderPositions[4] = DefaultModelParameters.default.topK.toFloat()
                        sliderPositions[5] = DefaultModelParameters.default.topP
                        sliderPositions[6] = DefaultModelParameters.default.minP
                        scope.launch {
                            snackbarHostState.showSnackbar(message = "The default parameters applied!")
                        }
                    }
                ) {
                    Text(text = "Reset Parameters")
                }

                Button(
                    onClick = {
                        onSaveClick(
                            "http://$ipAddress:$port",
                            selectedEmbeddingModel,
                            ModelParameters(
                                temperature = sliderPositions[0],
                                numCtx = sliderPositions[1].toInt(),
                                presencePenalty = sliderPositions[2],
                                frequencyPenalty = sliderPositions[3],
                                topK = sliderPositions[4].toInt(),
                                topP = sliderPositions[5],
                                minP = sliderPositions[6]
                            )
                        )
                        scope.launch {
                            snackbarHostState.showSnackbar(message = "The settings saved!")
                        }
                    }
                ) {
                    Text(text = "Save")
                }
            }
        }
        BackHandler {
            onBackClick()
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun SettingScreenPreview() {
    ULlamaTheme {
        SettingScreen(
            savedParameters = listOf("http://127.0.0.1:11434", "al-minilm","1.0","2048", "1.0", "1.5", "40", "1.0", "0.9"),
            embeddingModelList = listOf("all-minilm", "llama3.2"),
            isEmbeddingModelPulled = { true },
            ollamaStatus = "Ollama is Running",
            onSaveClick = {_,_,_-> },
            onBackClick = {},
            onCheckClick = {},
            onPullEmbeddingModelClick = {},
            onFetchEmbeddingModelClick = {}
        )
    }
}