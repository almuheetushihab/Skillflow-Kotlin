package com.example.skillflow.ui.features.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.manager.ReminderManager
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
    val isNotificationEnabled: Boolean = true,
    val reminderTime: Long = 21 * 3600 * 1000L, // Default 9:00 PM
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
    private val settingsRepository: SettingsRepository,
    private val reminderManager: ReminderManager
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
            val userDetailsFlow = combine(
                settingsRepository.getLanguage(),
                settingsRepository.getUserEmail(),
                settingsRepository.getUserName()
            ) { lang, email, name ->
                Triple(lang, email, name)
            }

            val reminderDetailsFlow = combine(
                settingsRepository.getLearningTime(),
                settingsRepository.isNotificationEnabled(),
                settingsRepository.getReminderTime()
            ) { time, notifEnabled, remTime ->
                Triple(time, notifEnabled, remTime)
            }

            combine(userDetailsFlow, reminderDetailsFlow) { (lang, email, name), (time, notifEnabled, remTime) ->
                _state.value.copy(
                    language = lang,
                    email = email ?: "",
                    name = name ?: "",
                    learningTime = time,
                    isNotificationEnabled = notifEnabled,
                    reminderTime = remTime
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationEnabled(enabled)
            if (enabled) {
                reminderManager.scheduleReminder(_state.value.reminderTime)
                _eventFlow.emit(SettingsUiEvent.ShowSnackbar("Daily reminders enabled"))
            } else {
                reminderManager.cancelReminder()
                _eventFlow.emit(SettingsUiEvent.ShowSnackbar("Daily reminders disabled"))
            }
        }
    }

    fun setReminderTime(timeInMillisFromMidnight: Long) {
        viewModelScope.launch {
            settingsRepository.setReminderTime(timeInMillisFromMidnight)
            if (_state.value.isNotificationEnabled) {
                reminderManager.scheduleReminder(timeInMillisFromMidnight)
            }
            _eventFlow.emit(SettingsUiEvent.ShowSnackbar("Reminder time updated"))
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
