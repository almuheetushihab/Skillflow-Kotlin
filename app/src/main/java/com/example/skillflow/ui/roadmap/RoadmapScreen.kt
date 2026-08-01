package com.example.skillflow.ui.roadmap

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skillflow.R
import com.example.skillflow.ui.theme.GradientEnd
import com.example.skillflow.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapScreen() {
    val steps = listOf(
        "Introduction to Android & Tools",
        "Kotlin Essentials for Developers",
        "Modern UI with Jetpack Compose",
        "Architecting with MVVM & Hilt",
        "Network Operations with Retrofit",
        "Local Storage & Room Database",
        "Advanced Animations & Polish",
        "Release & Deployment Strategy"
    )
    val currentStepIndex = 3

    val backgroundGradient = Brush.verticalGradient(
        listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    )

    Scaffold(
        modifier = Modifier.background(backgroundGradient),
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        stringResource(R.string.my_journey),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    ) 
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp)
        ) {
            itemsIndexed(steps) { index, step ->
                RoadmapStepItem(
                    title = step,
                    isCompleted = index < currentStepIndex,
                    isCurrent = index == currentStepIndex,
                    isLast = index == steps.size - 1
                )
            }
        }
    }
}

@Composable
fun RoadmapStepItem(
    title: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLast: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.width(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val color = when {
                isCompleted -> GradientStart
                isCurrent -> GradientStart
                else -> MaterialTheme.colorScheme.outlineVariant
            }

            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight()) {
                if (!isLast) {
                    Canvas(modifier = Modifier.fillMaxHeight().width(3.dp)) {
                        drawLine(
                            color = color.copy(alpha = 0.5f),
                            start = center.copy(y = 24.dp.toPx()),
                            end = center.copy(y = size.height),
                            strokeWidth = 6f
                        )
                    }
                }
                
                Box(contentAlignment = Alignment.Center, modifier = Modifier.align(Alignment.TopCenter)) {
                    if (isCurrent) {
                        Surface(
                            shape = CircleShape,
                            color = GradientStart.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp).graphicsLayer {
                                scaleX = pulseScale
                                scaleY = pulseScale
                            }
                        ) {}
                    }
                    
                    Surface(
                        shape = CircleShape,
                        color = if (isCompleted || isCurrent) Color.Transparent else color,
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                if (isCompleted || isCurrent) Brush.linearGradient(listOf(GradientStart, GradientEnd))
                                else Brush.linearGradient(listOf(color, color))
                            ),
                        border = if (isCurrent) Stroke(2f).let { null } else null
                    ) {
                        if (isCompleted) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) 
                                else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrent) 4.dp else 0.dp),
            border = if (isCurrent) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.SemiBold,
                    color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
                if (isCurrent) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.actively_learning),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
