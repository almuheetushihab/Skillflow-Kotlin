package com.example.skillflow.screens.commonComponents

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

/**
 * A reusable Modifier that applies a shimmer loading effect.
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer")
    val startPadding = 200f
    
    val translateAnim by transition.animateFloat(
        initialValue = -startPadding,
        targetValue = size.width.toFloat() + startPadding,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslation"
    )

    val baseColor = MaterialTheme.colorScheme.surfaceVariant
    val shimmerColors = listOf(
        baseColor.copy(alpha = 0.6f),
        baseColor.copy(alpha = 0.2f),
        baseColor.copy(alpha = 0.6f),
    )

    this.background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim - startPadding, translateAnim - startPadding),
            end = Offset(translateAnim, translateAnim)
        )
    ).onGloballyPositioned {
        size = it.size
    }
}
