package com.example.skillflow.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    fun login(email: String, pass: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            if (email.contains("@") && pass.length >= 6) {
                settingsRepository.setLoggedIn(true)
                _state.update { it.copy(isLoading = false, isLoggedIn = true) }
            } else {
                _state.update { it.copy(isLoading = false, error = "Invalid email or password") }
            }
        }
    }

    fun signUp(name: String, email: String, pass: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            if (email.contains("@") && pass.length >= 6) {
                settingsRepository.setLoggedIn(true)
                _state.update { it.copy(isLoading = false, isLoggedIn = true) }
            } else {
                _state.update { it.copy(isLoading = false, error = "Registration failed") }
            }
        }
    }
}
