package com.example.app_journey.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.clickable
import kotlinx.coroutines.launch

fun Modifier.clickBounce(onClick: () -> Unit): Modifier = composed {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    this.graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }.clickable {
        scope.launch {
            scale.snapTo(0.92f)
            scale.animateTo(1f, tween(150))
            onClick()
        }
    }
}
