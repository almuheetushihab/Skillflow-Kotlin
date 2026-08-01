package com.example.skillflow.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSelectedCareerPath(): Flow<String?>
    suspend fun saveSelectedCareerPath(careerPathId: String)
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
    fun getStreakCount(): Flow<Int>
    suspend fun updateStreak(count: Int)
    fun getLastActivityDate(): Flow<String?>
    suspend fun updateLastActivityDate(date: String)
}
