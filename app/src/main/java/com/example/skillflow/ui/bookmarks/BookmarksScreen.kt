package com.example.skillflow.ui.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skillflow.R
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.presentation.bookmarks.BookmarksState
import com.example.skillflow.presentation.bookmarks.BookmarksViewModel
import com.example.skillflow.ui.common.AnimatedEntrance
import com.example.skillflow.ui.common.EmptyView
import com.example.skillflow.ui.common.NuggetCard
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.home.components.NuggetCardSkeleton
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.spacing

@Composable
fun BookmarksScreen(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookmarksViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    BookmarksContent(
        state = state,
        onNavigateToDetail = onNavigateToDetail,
        modifier = modifier
    )
}

@Composable
fun BookmarksContent(
    state: BookmarksState,
    onNavigateToDetail: (String) -> Unit,
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
            SkillflowTopAppBar(title = stringResource(R.string.saved_nuggets))
        }
    ) { padding ->
        val spacing = MaterialTheme.spacing
        
        if (state.isLoading) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = spacing.large),
                verticalArrangement = Arrangement.spacedBy(spacing.medium),
                contentPadding = PaddingValues(top = spacing.medium, bottom = spacing.medium)
            ) {
                items(5) {
                    NuggetCardSkeleton()
                }
            }
        } else if (state.savedNuggets.isEmpty()) {
            EmptyView(
                message = stringResource(R.string.no_bookmarks),
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = spacing.large),
                verticalArrangement = Arrangement.spacedBy(spacing.medium),
                contentPadding = PaddingValues(
                    top = spacing.medium,
                    bottom = spacing.medium
                )
            ) {
                itemsIndexed(state.savedNuggets) { index, nugget ->
                    AnimatedEntrance(index = index) {
                        NuggetCard(
                            nugget = nugget,
                            onClick = { onNavigateToDetail(nugget.id) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookmarksContentPreview() {
    SkillflowTheme {
        BookmarksContent(
            state = BookmarksState(
                savedNuggets = listOf(
                    KnowledgeNugget(
                        id = "1",
                        title = "Kotlin Coroutines",
                        shortDescription = "Learn async programming",
                        content = "Full content of coroutines",
                        complexity = "Intermediate",
                        imageUrl = null,
                        careerPathId = "android",
                        isDone = false,
                        isSaved = true,
                        isMastered = false,
                        completionDate = null,
                        priority = 0,
                        date = "2026-08-02",
                        quizzes = emptyList()
                    )
                )
            ),
            onNavigateToDetail = {}
        )
    }
}
