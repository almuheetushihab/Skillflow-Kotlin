package com.example.skillflow.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val userName: String = "",
    val profilePictureUri: String? = null,
    val careerPathId: String? = null,
    val streakCount: Int = 0,
    val isDarkMode: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            settingsRepository.getSelectedCareerPath().collect { id ->
                _state.update { it.copy(careerPathId = id) }
            }
        }
        viewModelScope.launch {
            settingsRepository.getStreakCount().collect { count ->
                _state.update { it.copy(streakCount = count) }
            }
        }
        viewModelScope.launch {
            settingsRepository.getProfilePictureUri().collect { uri ->
                _state.update { it.copy(profilePictureUri = uri) }
            }
        }
        viewModelScope.launch {
            settingsRepository.getUserName().collect { name ->
                _state.update { it.copy(userName = name ?: "") }
            }
        }
    }

    fun setProfilePicture(uri: String) {
        viewModelScope.launch {
            settingsRepository.setProfilePictureUri(uri)
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted(false)
        }
    }

    fun toggleDarkMode() {
        _state.update { it.copy(isDarkMode = !it.isDarkMode) }
    }
}
