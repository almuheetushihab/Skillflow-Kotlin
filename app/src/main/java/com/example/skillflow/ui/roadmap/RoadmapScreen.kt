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
import com.example.skillflow.ui.common.ErrorView
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.roadmap.components.RoadmapStepItem
import com.example.skillflow.ui.roadmap.components.RoadmapStepSkeleton
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.spacing
import timber.log.Timber

/**
 * Screen displaying the user's career roadmap.
 */
@Composable
fun RoadmapScreen(
    modifier: Modifier = Modifier
) {
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
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        Timber.d("Loading roadmap steps")
        try {
            kotlinx.coroutines.delay(1500)
            isLoading = false
        } catch (e: Exception) {
            Timber.e(e, "Failed to load roadmap")
            error = e.localizedMessage
            isLoading = false
        }
    }

    RoadmapContent(
        steps = steps,
        currentStepIndex = currentStepIndex,
        isLoading = isLoading,
        error = error,
        onRetry = {
            isLoading = true
            error = null
        },
        modifier = modifier
    )
}

/**
 * The content of the Roadmap screen.
 */
@Composable
fun RoadmapContent(
    steps: List<String>,
    currentStepIndex: Int,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundGradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    )

    Scaffold(
        modifier = modifier.background(backgroundGradient),
        topBar = {
            SkillflowTopAppBar(title = stringResource(R.string.my_journey))
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (error != null) {
                ErrorView(message = error, onRetry = onRetry)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = MaterialTheme.spacing.large),
                    contentPadding = PaddingValues(top = MaterialTheme.spacing.large, bottom = MaterialTheme.spacing.large)
                ) {
                    if (isLoading) {
                        items(8) {
                            RoadmapStepSkeleton()
                        }
                    } else {
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoadmapContentPreview() {
    SkillflowTheme {
        RoadmapContent(
            steps = listOf("Step 1", "Step 2", "Step 3"),
            currentStepIndex = 1,
            isLoading = false,
            error = null,
            onRetry = {}
        )
    }
}
