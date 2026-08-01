package com.example.skillflow.ui.roadmap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapScreen() {
    // Mock data for roadmap steps
    val steps = listOf(
        "Introduction to Android",
        "Kotlin Basics",
        "Jetpack Compose Fundamentals",
        "MVVM Architecture",
        "Networking with Retrofit",
        "Local Database with Room",
        "Advanced UI & Animations"
    )
    val currentStepIndex = 2 // Mock current progress

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Career Roadmap") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.width(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val color = when {
                isCompleted -> MaterialTheme.colorScheme.primary
                isCurrent -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.outlineVariant
            }

            Box(contentAlignment = Alignment.Center) {
                if (!isLast) {
                    Canvas(modifier = Modifier.height(100.dp).width(2.dp)) {
                        drawLine(
                            color = color,
                            start = center.copy(y = 0f),
                            end = center.copy(y = size.height),
                            strokeWidth = 4f
                        )
                    }
                }
                Surface(
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = color,
                    modifier = Modifier.size(24.dp),
                    border = if (isCurrent) Stroke(2f).let { null } else null // Simplified
                ) {
                    // Circle content
                }
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrent) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                )
                if (isCurrent) {
                    Text(
                        text = "Current Learning Path",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
