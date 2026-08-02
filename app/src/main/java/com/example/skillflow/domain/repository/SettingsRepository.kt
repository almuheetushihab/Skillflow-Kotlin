package com.example.skillflow.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSelectedCareerPath(): Flow<String?>
    suspend fun saveSelectedCareerPath(careerPathId: String)
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
    fun isLoggedIn(): Flow<Boolean>
    suspend fun setLoggedIn(loggedIn: Boolean)
    fun getStreakCount(): Flow<Int>
    suspend fun updateStreak(count: Int)
    fun getLastActivityDate(): Flow<String?>
    suspend fun updateLastActivityDate(date: String)
    
    // New Feature Methods
    fun getLanguage(): Flow<String>
    suspend fun setLanguage(languageCode: String)
    fun getLearningTime(): Flow<Long>
    suspend fun addLearningTime(minutes: Long)
    fun getUserEmail(): Flow<String?>
    suspend fun setUserEmail(email: String)
    fun getUserName(): Flow<String?>
    suspend fun setUserName(name: String)
    fun getProfilePictureUri(): Flow<String?>
    suspend fun setProfilePictureUri(uri: String)
    fun isRememberMe(): Flow<Boolean>
    suspend fun setRememberMe(remember: Boolean)
    fun getQuizCount(): Flow<Int>
    suspend fun incrementQuizCount()
    fun getTotalQuizScore(): Flow<Int>
    suspend fun addToTotalQuizScore(score: Int)
    suspend fun clearSession()
}
