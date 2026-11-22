package com.example.skillflow.ui.screens.leaderboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skillflow.R
import com.example.skillflow.ui.common.LoadingView
import com.example.skillflow.ui.common.SkillflowTopAppBar
import com.example.skillflow.ui.screens.leaderboard.components.CurrentUserRankCard
import com.example.skillflow.ui.screens.leaderboard.components.LeaderboardListItem
import com.example.skillflow.ui.screens.leaderboard.components.PodiumComponent
import com.example.skillflow.ui.theme.OnPrimary
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.spacing

/**
 * Screen displaying the Social Leaderboard with Top 3 Podium and sticky user rank.
 */
@Composable
fun LeaderboardScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LeaderboardContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
fun LeaderboardContent(
    uiState: LeaderboardUiState,
    onNavigateBack: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val topThreeUsers = uiState.topUsers.take(3)
    val remainingUsers = uiState.topUsers.drop(3)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SkillflowTopAppBar(
                title = stringResource(R.string.social_leaderboard),
                navigationIcon = if (onNavigateBack != null) Icons.AutoMirrored.Filled.ArrowBack else null,
                onNavigationClick = { onNavigateBack?.invoke() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                LoadingView(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = spacing.large,
                        end = spacing.large,
                        top = spacing.medium,
                        bottom = 120.dp // Extra padding for pinned bottom card
                    ),
                    verticalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    // Title Banner
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(spacing.medium),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Leaderboard,
                                            contentDescription = stringResource(R.string.social_leaderboard),
                                            tint = OnPrimary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(spacing.medium))

                                Column {
                                    Text(
                                        text = stringResource(R.string.social_leaderboard),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = stringResource(R.string.leaderboard_subtitle),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Top 3 Podium
                    if (topThreeUsers.isNotEmpty()) {
                        item {
                            PodiumComponent(
                                topThreeUsers = topThreeUsers,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Remaining Users Header
                    if (remainingUsers.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(spacing.small))
                            Text(
                                text = stringResource(R.string.all_learning_material),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // List of Users (Rank 4 onwards)
                        items(
                            items = remainingUsers,
                            key = { it.id }
                        ) { user ->
                            LeaderboardListItem(
                                user = user,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Pinned Current User Rank Card at the bottom
                if (uiState.currentUser != null) {
                    CurrentUserRankCard(
                        currentUser = uiState.currentUser,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LeaderboardContentPreview() {
    SkillflowTheme {
        LeaderboardContent(
            uiState = LeaderboardUiState(),
            onNavigateBack = {}
        )
    }
}

