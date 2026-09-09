package com.example.skillflow.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.RestartAlt
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
import com.example.skillflow.viewModel.home.HomeState
import com.example.skillflow.viewModel.home.HomeViewModel
import com.example.skillflow.screens.commonComponents.AnimatedEntrance
import com.example.skillflow.screens.commonComponents.NuggetCard
import com.example.skillflow.screens.home.components.BentoGrid
import com.example.skillflow.screens.home.components.CategoryPills
import com.example.skillflow.screens.home.components.NuggetCardSkeleton
import com.example.skillflow.screens.home.components.ProgressCardSkeleton
import com.example.skillflow.screens.theme.GradientStart
import com.example.skillflow.screens.theme.SkillflowTheme
import com.example.skillflow.screens.theme.spacing
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToBookmarks: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    HomeContent(
        state = state,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToBookmarks = onNavigateToBookmarks,
        onDateSelected = viewModel::onDateSelected,
        onRefresh = viewModel::refresh,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeState,
    onSearchQueryChange: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onDateSelected: (String?) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val spacing = MaterialTheme.spacing
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
                        onDateSelected(date)
                    }
                    showDatePicker = false
                }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    onDateSelected(null)
                    showDatePicker = false 
                }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.app_name),
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.displaySmall
                        )
                        Text(
                            text = if (state.selectedDate == null) 
                                stringResource(R.string.all_learning_material) 
                            else 
                                stringResource(R.string.history_for, state.selectedDate!!),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = stringResource(R.string.select_date),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = stringResource(R.string.refresh),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.large),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + spacing.small,
                bottom = padding.calculateBottomPadding() + spacing.extraLarge
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            // Bento Grid Header Section
            if (!state.isSearching) {
                item {
                    if (state.isLoading) {
                        ProgressCardSkeleton()
                    } else {
                        BentoGrid(
                            completedCount = state.totalLearned,
                            totalCount = state.totalCount,
                            streakCount = state.streakCount,
                            savedCount = state.savedCount,
                            onSavedTileClick = onNavigateToBookmarks
                        )
                    }
                }
            }

            // Search Box
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 2.dp
                ) {
                    TextField(
                        value = state.searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = stringResource(R.string.search_hint)) },
                        leadingIcon = { 
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = GradientStart
                            ) 
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }

            if (!state.isSearching) {
                // Category Pills Section
                item {
                    CategoryPills(
                        selectedDate = state.selectedDate,
                        availableDates = state.availableDates,
                        onDateSelected = onDateSelected,
                        onOpenDatePicker = { showDatePicker = true }
                    )
                }

                // Header for Lessons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.selectedDate != null) 
                                stringResource(R.string.learned_on_this_day) 
                            else 
                                stringResource(R.string.all_lessons),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = stringResource(R.string.search_results),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = spacing.small)
                    )
                }
            }

            if (state.isLoading) {
                items(5) {
                    NuggetCardSkeleton()
                }
            } else {
                val displayList = if (state.isSearching) state.searchResults else state.dailyNuggets
                
                if (displayList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxHeight(0.35f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (state.selectedDate != null) 
                                    stringResource(R.string.no_activity_for_date) 
                                else 
                                    stringResource(R.string.no_nuggets_found),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                } else {
                    itemsIndexed(displayList) { index, nugget ->
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

@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    SkillflowTheme {
        HomeContent(
            state = HomeState(
                dailyNuggets = listOf(
                    KnowledgeNugget("1", "Title 1", "Desc", "Content", "Beginner", null, "android", false, false, false, null, 0, "2026-08-01")
                ),
                totalLearned = 5,
                totalCount = 15,
                streakCount = 7,
                savedCount = 12
            ),
            onSearchQueryChange = {},
            onNavigateToDetail = {},
            onNavigateToBookmarks = {},
            onDateSelected = {},
            onRefresh = {}
        )
    }
}
