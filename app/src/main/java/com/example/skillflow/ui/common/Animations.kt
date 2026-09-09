package com.example.skillflow.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.skillflow.ui.theme.SkillflowTheme
import kotlinx.coroutines.delay

@Composable
fun AnimatedEntrance(
    modifier: Modifier = Modifier,
    index: Int = 0,
    content: @Composable () -> Unit
) {
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(index * 100L)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Box(
        modifier = modifier.graphicsLayer {
            alpha = animatedProgress.value
            scaleX = 0.8f + (animatedProgress.value * 0.2f)
            scaleY = 0.8f + (animatedProgress.value * 0.2f)
            translationY = (1f - animatedProgress.value) * 40.dp.toPx()
        }
    ) {
        content()
    }
}

@Composable
fun PulseAnimation(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun AnimationsPreview() {
    SkillflowTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AnimatedEntrance(index = 0) {
                Text(text = "Animated Item")
            }
        }
    }
}
