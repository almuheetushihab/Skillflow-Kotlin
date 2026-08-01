package com.example.skillflow.ui.roadmap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.skillflow.R
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.roadmap.components.RoadmapStepItem
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.spacing

@Composable
fun RoadmapScreen() {
    // In a real app, this would come from a ViewModel
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

    RoadmapContent(
        steps = steps,
        currentStepIndex = currentStepIndex
    )
}

@Composable
fun RoadmapContent(
    steps: List<String>,
    currentStepIndex: Int
) {
    val backgroundGradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    )

    Scaffold(
        modifier = Modifier.background(backgroundGradient),
        topBar = {
            SkillflowTopAppBar(title = stringResource(R.string.my_journey))
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = MaterialTheme.spacing.large),
            contentPadding = PaddingValues(top = MaterialTheme.spacing.large, bottom = MaterialTheme.spacing.large)
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

@Preview(showBackground = true)
@Composable
fun RoadmapContentPreview() {
    SkillflowTheme {
        RoadmapContent(
            steps = listOf("Step 1", "Step 2", "Step 3"),
            currentStepIndex = 1
        )
    }
}
