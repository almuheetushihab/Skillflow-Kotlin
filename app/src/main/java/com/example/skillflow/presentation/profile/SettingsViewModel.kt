package com.example.skillflow.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val language: String = "en",
    val email: String = "",
    val name: String = "",
    val learningTime: Long = 0,
    val isLoading: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                settingsRepository.getLanguage(),
                settingsRepository.getUserEmail(),
                settingsRepository.getUserName(),
                settingsRepository.getLearningTime()
            ) { lang, email, name, time ->
                SettingsState(
                    language = lang,
                    email = email ?: "",
                    name = name ?: "",
                    learningTime = time
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            // Update local state immediately for smooth animation
            _state.update { it.copy(language = lang) }
            // Delay the repository update which triggers activity recreation
            delay(400) 
            settingsRepository.setLanguage(lang)
        }
    }

    fun updateEmail(newEmail: String) {
        viewModelScope.launch {
            settingsRepository.setUserEmail(newEmail)
            _state.update { it.copy(message = "Email updated successfully") }
        }
    }

    fun updateName(newName: String) {
        viewModelScope.launch {
            settingsRepository.setUserName(newName)
            _state.update { it.copy(message = "Name updated successfully") }
        }
    }

    fun logout() {
        viewModelScope.launch {
            settingsRepository.clearSession()
        }
    }
}
