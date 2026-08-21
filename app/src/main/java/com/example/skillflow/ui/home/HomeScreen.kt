package com.example.skillflow.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalFireDepartment
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
import com.example.skillflow.presentation.home.HomeState
import com.example.skillflow.presentation.home.HomeViewModel
import com.example.skillflow.ui.common.AnimatedEntrance
import com.example.skillflow.ui.common.NuggetCard
import com.example.skillflow.ui.home.components.DailyProgressCard
import com.example.skillflow.ui.home.components.NuggetCardSkeleton
import com.example.skillflow.ui.home.components.ProgressCardSkeleton
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.SunsetEnd
import com.example.skillflow.ui.theme.spacing
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    HomeContent(
        state = state,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onNavigateToDetail = onNavigateToDetail,
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
                                stringResource(R.string.history_for, state.selectedDate),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = stringResource(R.string.select_date)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(spacing.extraLarge),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = spacing.medium)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = spacing.medium - 2.dp, vertical = spacing.extraSmall + 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = stringResource(R.string.streak),
                                tint = SunsetEnd,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(spacing.extraSmall + 2.dp))
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
                top = padding.calculateTopPadding() + spacing.medium,
                bottom = padding.calculateBottomPadding() + spacing.extraLarge
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(spacing.extraLarge),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
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
                item {
                    Spacer(modifier = Modifier.height(spacing.extraSmall))
                    if (state.isLoading) {
                        ProgressCardSkeleton()
                    } else {
                        DailyProgressCard(completedCount = state.totalLearned, totalCount = state.totalCount)
                    }
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                }

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
                        IconButton(onClick = onRefresh) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = stringResource(R.string.refresh),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
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
                                .fillParentMaxHeight(0.6f)
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
                totalCount = 15
            ),
            onSearchQueryChange = {},
            onNavigateToDetail = {},
            onDateSelected = {},
            onRefresh = {}
        )
    }
}
