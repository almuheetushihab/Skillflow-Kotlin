package com.example.skillflow.ui.screens.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.model.LeaderboardUser
import com.example.skillflow.domain.repository.LeaderboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * State representing the Social Leaderboard UI.
 */
data class LeaderboardUiState(
    val topUsers: List<LeaderboardUser> = emptyList(),
    val currentUser: LeaderboardUser? = null,
    val isLoading: Boolean = true
)

/**
 * ViewModel for managing Social Leaderboard state and user rankings.
 */
@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    repository: LeaderboardRepository
) : ViewModel() {

    val uiState: StateFlow<LeaderboardUiState> = combine(
        repository.getTopUsers(),
        repository.getCurrentUserRank()
    ) { users, currentUser ->
        LeaderboardUiState(
            topUsers = users,
            currentUser = currentUser,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LeaderboardUiState()
    )
}
