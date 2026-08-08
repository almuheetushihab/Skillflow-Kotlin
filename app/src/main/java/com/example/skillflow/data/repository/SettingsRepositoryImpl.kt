package com.example.skillflow.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.skillflow.data.local.SkillDatabase
import com.example.skillflow.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val database: SkillDatabase
) : SettingsRepository {

    private object PreferencesKeys {
        val CAREER_PATH_ID = stringPreferencesKey("career_path_id")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val STREAK_COUNT = intPreferencesKey("streak_count")
        val LAST_ACTIVITY_DATE = stringPreferencesKey("last_activity_date")
    }

    override fun getSelectedCareerPath(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.CAREER_PATH_ID]
        }
    }

    override suspend fun saveSelectedCareerPath(careerPathId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CAREER_PATH_ID] = careerPathId
        }
    }

    override fun isOnboardingCompleted(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
        }
    }

    override suspend fun setLoggedIn(loggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = loggedIn
        }
    }

    override fun getStreakCount(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.STREAK_COUNT] ?: 0
        }
    }

    override suspend fun updateStreak(count: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.STREAK_COUNT] = count
        }
    }

    override fun getLastActivityDate(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.LAST_ACTIVITY_DATE]
        }
    }

    override suspend fun updateLastActivityDate(date: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_ACTIVITY_DATE] = date
        }
    }

    private object NewPreferencesKeys {
        val LANGUAGE = stringPreferencesKey("language")
        val LEARNING_TIME = intPreferencesKey("learning_time") // Store in minutes
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NAME = stringPreferencesKey("user_name")
        val PROFILE_PICTURE_URI = stringPreferencesKey("profile_picture_uri")
        val IS_REMEMBER_ME = booleanPreferencesKey("is_remember_me")
        val QUIZ_COUNT = intPreferencesKey("quiz_count")
        val TOTAL_QUIZ_SCORE = intPreferencesKey("total_quiz_score")
    }

    override fun getLanguage(): Flow<String> = dataStore.data.map { it[NewPreferencesKeys.LANGUAGE] ?: "en" }

    override suspend fun setLanguage(languageCode: String) {
        dataStore.edit { it[NewPreferencesKeys.LANGUAGE] = languageCode }
    }

    override fun getLearningTime(): Flow<Long> = dataStore.data.map { (it[NewPreferencesKeys.LEARNING_TIME] ?: 0).toLong() }

    override suspend fun addLearningTime(minutes: Long) {
        dataStore.edit { 
            val current = it[NewPreferencesKeys.LEARNING_TIME] ?: 0
            it[NewPreferencesKeys.LEARNING_TIME] = current + minutes.toInt()
        }
    }

    override fun getUserEmail(): Flow<String?> = dataStore.data.map { it[NewPreferencesKeys.USER_EMAIL] }

    override suspend fun setUserEmail(email: String) {
        dataStore.edit { it[NewPreferencesKeys.USER_EMAIL] = email }
    }

    override fun getUserName(): Flow<String?> = dataStore.data.map { it[NewPreferencesKeys.USER_NAME] }

    override suspend fun setUserName(name: String) {
        dataStore.edit { it[NewPreferencesKeys.USER_NAME] = name }
    }

    override fun getProfilePictureUri(): Flow<String?> = dataStore.data.map { it[NewPreferencesKeys.PROFILE_PICTURE_URI] }

    override suspend fun setProfilePictureUri(uri: String) {
        dataStore.edit { it[NewPreferencesKeys.PROFILE_PICTURE_URI] = uri }
    }

    override fun isRememberMe(): Flow<Boolean> = dataStore.data.map { it[NewPreferencesKeys.IS_REMEMBER_ME] ?: false }

    override suspend fun setRememberMe(remember: Boolean) {
        dataStore.edit { it[NewPreferencesKeys.IS_REMEMBER_ME] = remember }
    }

    override fun getQuizCount(): Flow<Int> = dataStore.data.map { it[NewPreferencesKeys.QUIZ_COUNT] ?: 0 }

    override suspend fun incrementQuizCount() {
        dataStore.edit { 
            val current = it[NewPreferencesKeys.QUIZ_COUNT] ?: 0
            it[NewPreferencesKeys.QUIZ_COUNT] = current + 1
        }
    }

    override fun getTotalQuizScore(): Flow<Int> = dataStore.data.map { it[NewPreferencesKeys.TOTAL_QUIZ_SCORE] ?: 0 }

    override suspend fun addToTotalQuizScore(score: Int) {
        dataStore.edit { 
            val current = it[NewPreferencesKeys.TOTAL_QUIZ_SCORE] ?: 0
            it[NewPreferencesKeys.TOTAL_QUIZ_SCORE] = current + score
        }
    }

    override suspend fun clearSession() {
        dataStore.edit { it.clear() }
        database.clearAllTables()
    }
}
