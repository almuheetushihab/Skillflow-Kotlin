package com.example.skillflow.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.repository.AuthRepository
import com.example.skillflow.domain.repository.SettingsRepository
import com.example.skillflow.domain.util.Resource
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
    val error: String? = null,
    val isAccountDeleted: Boolean = false,
    val isLoggedOut: Boolean = false
)

sealed class SettingsUiEvent {
    data class ShowSnackbar(val message: String) : SettingsUiEvent()
    object LogoutSuccess : SettingsUiEvent()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<SettingsUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

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
                _state.value.copy(
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
            _state.update { it.copy(language = lang) }
            delay(400) 
            settingsRepository.setLanguage(lang)
        }
    }

    fun updateEmail(newEmail: String) {
        viewModelScope.launch {
            settingsRepository.setUserEmail(newEmail)
            _eventFlow.emit(SettingsUiEvent.ShowSnackbar("Email updated locally"))
        }
    }

    fun updateName(newName: String) {
        viewModelScope.launch {
            settingsRepository.setUserName(newName)
            _eventFlow.emit(SettingsUiEvent.ShowSnackbar("Name updated locally"))
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout().collect { result ->
                if (result is Resource.Success) {
                    settingsRepository.clearSession()
                    _state.update { it.copy(isLoggedOut = true) }
                    _eventFlow.emit(SettingsUiEvent.LogoutSuccess)
                }
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            authRepository.deleteAccount().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        settingsRepository.clearSession()
                        _state.update { it.copy(isLoading = false, isAccountDeleted = true) }
                        _eventFlow.emit(SettingsUiEvent.LogoutSuccess)
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                        _eventFlow.emit(SettingsUiEvent.ShowSnackbar(result.message ?: "Deletion failed"))
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                }
            }
        }
    }
}
