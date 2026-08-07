package com.example.skillflow.ui.detail

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skillflow.R
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.presentation.detail.DetailState
import com.example.skillflow.presentation.detail.DetailViewModel
import com.example.skillflow.ui.common.LoadingView
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.detail.components.KnowledgeCard
import com.example.skillflow.ui.theme.GradientEnd
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.spacing
import kotlinx.coroutines.delay

@Composable
fun DetailScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    DetailContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onToggleSave = viewModel::toggleSave,
        onFlipCard = viewModel::flipCard,
        onMarkAsDone = viewModel::markAsDone,
        onTrackTime = { viewModel.trackLearningTime(it) },
        modifier = modifier
    )
}

@Composable
fun DetailContent(
    state: DetailState,
    onNavigateBack: () -> Unit,
    onToggleSave: () -> Unit,
    onFlipCard: () -> Unit,
    onMarkAsDone: () -> Unit,
    onTrackTime: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val nugget = state.nugget

    val rotation by animateFloatAsState(
        targetValue = if (state.isFlipped) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "CardRotation"
    )

    val backgroundGradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    )

    Scaffold(
        modifier = modifier.background(backgroundGradient),
        topBar = {
            SkillflowTopAppBar(
                title = stringResource(R.string.knowledge_nugget),
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack,
                actions = {
                    if (nugget != null) {
                        IconButton(onClick = onToggleSave) {
                            Icon(
                                imageVector = if (nugget.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Save",
                                tint = if (nugget.isSaved) GradientStart else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(MaterialTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (nugget == null) {
                if (state.isLoading) {
                    LoadingView(modifier = Modifier.fillMaxSize())
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.nugget_not_found))
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.tap_to_reveal),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

                KnowledgeCard(
                    nugget = nugget,
                    isFlipped = state.isFlipped,
                    rotation = rotation,
                    onFlip = onFlipCard
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                
                // Track reading time
                LaunchedEffect(Unit) {
                    delay(60000) // 1 minute
                    onTrackTime(1)
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onMarkAsDone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            if (!nugget.isDone) Brush.linearGradient(listOf(GradientStart, GradientEnd))
                            else Brush.linearGradient(listOf(Color.Gray, Color.LightGray))
                        ),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    enabled = !nugget.isDone
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                    Text(
                        text = if (nugget.isDone) stringResource(R.string.knowledge_mastered) else stringResource(R.string.mark_as_learned),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailContentPreview() {
    SkillflowTheme {
        DetailContent(
            state = DetailState(
                nugget = KnowledgeNugget(
                    "1", "Kotlin Coroutines", "Full content of coroutines", null, "android", false, false, "2026-08-02"
                )
            ),
            onNavigateBack = {},
            onToggleSave = {},
            onFlipCard = {},
            onMarkAsDone = {},
            onTrackTime = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailContentLoadingPreview() {
    SkillflowTheme {
        DetailContent(
            state = DetailState(isLoading = true),
            onNavigateBack = {},
            onToggleSave = {},
            onFlipCard = {},
            onMarkAsDone = {},
            onTrackTime = {}
        )
    }
}
