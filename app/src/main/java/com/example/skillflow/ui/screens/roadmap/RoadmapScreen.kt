package com.example.skillflow.ui.screens.roadmap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skillflow.R
import com.example.skillflow.ui.common.EmptyView
import com.example.skillflow.ui.common.ErrorView
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.screens.roadmap.components.RoadmapStepItem
import com.example.skillflow.ui.screens.roadmap.components.RoadmapStepSkeleton
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.OnPrimary
import com.example.skillflow.ui.theme.spacing

/**
 * Screen displaying the user's career roadmap dynamically for their active learning goal.
 */
@Composable
fun RoadmapScreen(
    onNavigateToDetail: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: RoadmapViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    RoadmapContent(
        state = state,
        onStepClick = onNavigateToDetail,
        onAddTopic = viewModel::addCustomStep,
        onRetry = viewModel::loadRoadmap,
        modifier = modifier
    )
}

@Composable
fun RoadmapContent(
    state: RoadmapState,
    onStepClick: (String) -> Unit,
    onAddTopic: (String, String, String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val backgroundGradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    )

    val titleText = if (state.careerPathName.isNotBlank()) {
        "${state.careerPathName} ${stringResource(R.string.nav_roadmap)}"
    } else {
        stringResource(R.string.career_roadmap)
    }

    Scaffold(
        modifier = modifier.background(backgroundGradient),
        topBar = {
            SkillflowTopAppBar(
                title = titleText,
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_topic),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_topic),
                        tint = OnPrimary
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.add_topic),
                        fontWeight = FontWeight.Bold,
                        color = OnPrimary
                    )
                },
                containerColor = GradientStart
            )
        }
    ) { padding ->
        val spacing = MaterialTheme.spacing
        
        if (state.error != null) {
            ErrorView(message = state.error, onRetry = onRetry)
        } else if (state.isLoading) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = spacing.large),
                contentPadding = PaddingValues(top = spacing.large, bottom = 100.dp)
            ) {
                items(6) {
                    RoadmapStepSkeleton()
                }
            }
        } else if (state.steps.isEmpty()) {
            EmptyView(
                message = stringResource(R.string.no_nuggets_found),
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = spacing.large),
                contentPadding = PaddingValues(top = spacing.large, bottom = 100.dp)
            ) {
                itemsIndexed(state.steps) { index, step ->
                    RoadmapStepItem(
                        title = step.title,
                        isCompleted = step.isCompleted,
                        isCurrent = step.isCurrent,
                        isLast = index == state.steps.size - 1,
                        onClick = { onStepClick(step.id) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddTopicDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { title, desc, content ->
                    onAddTopic(title, desc, content)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AddTopicDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.add_custom_topic),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.topic_title)) },
                    placeholder = { Text(stringResource(R.string.enter_topic_title)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.topic_desc)) },
                    placeholder = { Text(stringResource(R.string.enter_topic_desc)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text(stringResource(R.string.topic_content)) },
                    placeholder = { Text(stringResource(R.string.enter_topic_content)) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, description, content)
                    }
                },
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(R.string.add_topic))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun RoadmapContentPreview() {
    com.example.skillflow.ui.theme.SkillflowTheme {
        RoadmapContent(
            state = RoadmapState(
                steps = listOf(
                    RoadmapStepUiModel("1", "Step 1", isCompleted = true, isCurrent = false),
                    RoadmapStepUiModel("2", "Step 2", isCompleted = false, isCurrent = true),
                    RoadmapStepUiModel("3", "Step 3", isCompleted = false, isCurrent = false)
                ),
                careerPathName = "Mentors' Foundation Grammar",
                isLoading = false
            ),
            onStepClick = {},
            onAddTopic = { _, _, _ -> },
            onRetry = {}
        )
    }
}
