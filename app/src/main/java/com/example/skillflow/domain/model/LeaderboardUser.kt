package com.example.skillflow.domain.model

/**
 * Domain model representing a user on the Social Leaderboard.
 *
 * @property id Unique identifier for the user.
 * @property name Display name of the user.
 * @property avatarUrl Optional profile image URL.
 * @property xp Experience Points earned by the user.
 * @property rank Leaderboard rank position (1-based).
 * @property isCurrentUser Flag indicating if this entry represents the active user.
 */
data class LeaderboardUser(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val xp: Int,
    val rank: Int = 0,
    val isCurrentUser: Boolean = false
)
