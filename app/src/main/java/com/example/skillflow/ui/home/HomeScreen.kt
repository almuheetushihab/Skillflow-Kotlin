package com.example.skillflow

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
import com.example.skillflow.ui.theme.GradientStart
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.SunsetEnd
import com.example.skillflow.ui.theme.spacing
import java.text.SimpleDateFormat
import java.util.Calendar
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
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { 
                    onDateSelected(null)
                    showDatePicker = false 
                }) { Text("Clear Filter") }
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
                            text = if (state.selectedDate == null) "All Learning Material" else "History for ${state.selectedDate}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Select Date")
                    }
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = MaterialTheme.spacing.medium)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = SunsetEnd, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "${state.streakCount}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = SunsetEnd)
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = MaterialTheme.spacing.large)
            ) {
                // Search Bar
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = state.searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = stringResource(R.string.search_hint)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GradientStart) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

                if (state.isSearching) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)) {
                        itemsIndexed(state.searchResults) { _, nugget ->
                            NuggetCard(nugget = nugget, onClick = { onNavigateToDetail(nugget.id) })
                        }
                    }
                } else {
                    DailyProgressCard(completedCount = state.totalLearned, totalCount = state.totalCount)

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (state.selectedDate != null) "Learned on this day" else "Your Learning Path",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        IconButton(onClick = onRefresh) {
                            Icon(Icons.Default.RestartAlt, contentDescription = "Refresh", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                    if (state.isLoading) {
                        repeat(3) { NuggetCardSkeleton() }
                    } else if (state.dailyNuggets.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (state.selectedDate != null) "You didn't master any nuggets on this date." else "No nuggets found.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            itemsIndexed(state.dailyNuggets) { index, nugget ->
                                AnimatedEntrance(index = index) {
                                    NuggetCard(nugget = nugget, onClick = { onNavigateToDetail(nugget.id) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
