package com.example.skillflow.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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
}
