package com.example.skillflow.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.skillflow.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
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
}
