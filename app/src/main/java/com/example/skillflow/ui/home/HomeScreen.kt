package com.example.skillflow.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skillflow.R
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.presentation.home.HomeState
import com.example.skillflow.presentation.home.HomeViewModel
import com.example.skillflow.ui.common.AnimatedEntrance
import com.example.skillflow.ui.common.LoadingView
import com.example.skillflow.ui.common.NuggetCard
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.home.components.DailyProgressCard
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.SunsetEnd
import com.example.skillflow.ui.theme.spacing

@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    HomeContent(
        state = state,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onNavigateToDetail = onNavigateToDetail
    )
}

@Composable
fun HomeContent(
    state: HomeState,
    onSearchQueryChange: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    )

    Scaffold(
        modifier = Modifier.background(backgroundGradient),
        topBar = {
            SkillflowTopAppBar(
                title = stringResource(R.string.app_name),
                actions = {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                        modifier = Modifier.padding(end = MaterialTheme.spacing.medium)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = stringResource(R.string.streak),
                                tint = SunsetEnd
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${state.streakCount}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SunsetEnd
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
                .padding(MaterialTheme.spacing.large)
        ) {
            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GradientStart,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            if (state.isSearching) {
                Text(
                    text = stringResource(R.string.search_results),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(state.searchResults) { index, nugget ->
                        AnimatedEntrance(index = index) {
                            NuggetCard(
                                nugget = nugget,
                                onClick = { onNavigateToDetail(nugget.id) }
                            )
                        }
                    }
                }
            } else {
                val completedCount = state.dailyNuggets.count { it.isDone }
                val totalCount = state.dailyNuggets.size

                DailyProgressCard(
                    completedCount = completedCount,
                    totalCount = totalCount
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))

                Text(
                    text = stringResource(R.string.todays_nuggets),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                if (state.isLoading) {
                    LoadingView(modifier = Modifier.fillMaxSize())
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(state.dailyNuggets) { index, nugget ->
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
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    SkillflowTheme {
        HomeContent(
            state = HomeState(
                streakCount = 5,
                dailyNuggets = listOf(
                    KnowledgeNugget("1", "Kotlin Coroutines", "Full content of coroutines", null, "android", false, false, "2026-08-02"),
                    KnowledgeNugget("2", "Compose Layouts", "Full content of layouts", null, "android", true, false, "2026-08-02")
                )
            ),
            onSearchQueryChange = {},
            onNavigateToDetail = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentLoadingPreview() {
    SkillflowTheme {
        HomeContent(
            state = HomeState(isLoading = true),
            onSearchQueryChange = {},
            onNavigateToDetail = {}
        )
    }
}
