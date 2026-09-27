package com.example.skillflow.domain.repository

import com.example.skillflow.domain.model.LeaderboardUser
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for retrieving social leaderboard data.
 */
interface LeaderboardRepository {
    /**
     * Retrieves the list of top-ranked users sorted by XP in descending order.
     */
    fun getTopUsers(): Flow<List<LeaderboardUser>>

    /**
     * Retrieves the rank and leaderboard details for the currently logged-in user.
     */
    fun getCurrentUserRank(): Flow<LeaderboardUser>
}
