package com.example.ollamaui.ui.screen.chat.components


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollamaui.ui.theme.OllamaUITheme
import kotlinx.coroutines.delay

@Composable
fun PulsingDots() {
    val scales = remember { List(3) { Animatable(0f) } }
    val color = MaterialTheme.colorScheme.primaryContainer

    LaunchedEffect(Unit) {
        while (true) {
            for (i in scales.indices) {
                scales[i].animateTo(
                    targetValue = 1.2f,
                    animationSpec = tween(durationMillis = 100, easing = LinearEasing)
                )
                scales[i].animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 100, easing = LinearEasing)
                )
                delay(50)
            }
            delay(100)
        }
    }
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (scale in scales) {
            Canvas(modifier = Modifier.size(10.dp)) {
                drawPulsingDot(scale = scale.value, color = color)
            }
        }
    }
}

private fun DrawScope.drawPulsingDot(scale: Float, color: Color) {
    val radius = 4.dp.toPx() * scale
    drawCircle(
        color = color,
        radius = radius,
        center = center
    )
}

@Preview
@Composable
private fun ChatLoadingPreview() {
    OllamaUITheme {
        PulsingDots()
    }
}