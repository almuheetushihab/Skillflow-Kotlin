package com.example.skillflow.ui.home

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeState,
    onSearchQueryChange: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.displaySmall
                    )
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = MaterialTheme.spacing.medium)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = stringResource(R.string.streak),
                                tint = SunsetEnd,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${state.streakCount}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = SunsetEnd
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
                windowInsets = WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = MaterialTheme.spacing.large)
        ) {
            // Refined Search Bar
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = state.searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { 
                        Text(
                            text = stringResource(R.string.search_hint),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        ) 
                    },
                    leadingIcon = { 
                        Icon(
                            Icons.Default.Search, 
                            contentDescription = null,
                            tint = GradientStart
                        ) 
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

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
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
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
