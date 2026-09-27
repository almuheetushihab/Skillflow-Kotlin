package com.example.skillflow.data.repository

import com.example.skillflow.domain.model.LeaderboardUser
import com.example.skillflow.domain.repository.LeaderboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fake implementation of [LeaderboardRepository] producing mock leaderboard data.
 * Used for testing and initial UI development before Firestore integration.
 */
@Singleton
class FakeLeaderboardRepositoryImpl @Inject constructor() : LeaderboardRepository {

    private val rawUsers = listOf(
        LeaderboardUser(id = "user_1", name = "Sophia Chen", avatarUrl = null, xp = 4850),
        LeaderboardUser(id = "user_2", name = "Marcus Vance", avatarUrl = null, xp = 4320),
        LeaderboardUser(id = "user_3", name = "Elena Rostova", avatarUrl = null, xp = 3980),
        LeaderboardUser(id = "user_4", name = "David Miller", avatarUrl = null, xp = 3650),
        LeaderboardUser(id = "user_5", name = "Alex River (You)", avatarUrl = null, xp = 3400, isCurrentUser = true),
        LeaderboardUser(id = "user_6", name = "Emily Watson", avatarUrl = null, xp = 3120),
        LeaderboardUser(id = "user_7", name = "Michael Brown", avatarUrl = null, xp = 2890),
        LeaderboardUser(id = "user_8", name = "Sarah Connor", avatarUrl = null, xp = 2650),
        LeaderboardUser(id = "user_9", name = "James Wilson", avatarUrl = null, xp = 2400),
        LeaderboardUser(id = "user_10", name = "Olivia Taylor", avatarUrl = null, xp = 2150),
        LeaderboardUser(id = "user_11", name = "Daniel Anderson", avatarUrl = null, xp = 1900),
        LeaderboardUser(id = "user_12", name = "Emma Thomas", avatarUrl = null, xp = 1680),
        LeaderboardUser(id = "user_13", name = "Lucas Jackson", avatarUrl = null, xp = 1450),
        LeaderboardUser(id = "user_14", name = "Ava White", avatarUrl = null, xp = 1200),
        LeaderboardUser(id = "user_15", name = "Ethan Harris", avatarUrl = null, xp = 950)
    )

    private val sortedLeaderboard: List<LeaderboardUser> = rawUsers
        .sortedByDescending { it.xp }
        .mapIndexed { index, user ->
            user.copy(rank = index + 1)
        }

    override fun getTopUsers(): Flow<List<LeaderboardUser>> {
        return flowOf(sortedLeaderboard)
    }

    override fun getCurrentUserRank(): Flow<LeaderboardUser> {
        val currentUser = sortedLeaderboard.find { it.isCurrentUser }
            ?: sortedLeaderboard.first()
        return flowOf(currentUser)
    }
}
