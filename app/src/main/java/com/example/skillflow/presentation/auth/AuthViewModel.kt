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
    val phoneNumber: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val isRememberMe: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val phoneError: String? = null,
    val nameError: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.isRememberMe().collect { remember ->
                _state.update { it.copy(isRememberMe = remember) }
            }
        }
    }

    fun toggleRememberMe() {
        viewModelScope.launch {
            val next = !state.value.isRememberMe
            settingsRepository.setRememberMe(next)
        }
    }

    fun login(email: String, pass: String) {
        if (!validateLogin(email, pass)) return

        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            // Mock login logic - allow any valid format for now but check common failure
            if (email.contains("fail")) {
                _state.update { it.copy(isLoading = false, error = "Account not found. Please sign up first.") }
            } else {
                settingsRepository.setLoggedIn(true)
                settingsRepository.setUserEmail(email)
                settingsRepository.setUserName(email.substringBefore("@").replaceFirstChar { it.uppercase() })
                _state.update { it.copy(isLoading = false, isLoggedIn = true) }
            }
        }
    }

    fun signUp(name: String, email: String, phone: String, pass: String) {
        if (!validateSignUp(name, email, phone, pass)) return

        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            settingsRepository.setLoggedIn(true)
            settingsRepository.setUserName(name)
            settingsRepository.setUserEmail(email)
            _state.update { it.copy(isLoading = false, isLoggedIn = true) }
        }
    }

    private fun validateLogin(email: String, pass: String): Boolean {
        var isValid = true
        _state.update { it.copy(emailError = null, passwordError = null) }

        if (email.isBlank()) {
            _state.update { it.copy(emailError = "Please enter your email") }
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.update { it.copy(emailError = "Invalid email format (e.g., user@example.com)") }
            isValid = false
        }

        if (pass.isBlank()) {
            _state.update { it.copy(passwordError = "Please enter your password") }
            isValid = false
        }

        return isValid
    }

    private fun validateSignUp(name: String, email: String, phone: String, pass: String): Boolean {
        var isValid = true
        _state.update { it.copy(nameError = null, emailError = null, phoneError = null, passwordError = null) }

        if (name.isBlank()) {
            _state.update { it.copy(nameError = "Please enter your full name") }
            isValid = false
        }

        if (email.isBlank()) {
            _state.update { it.copy(emailError = "Please enter your email") }
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.update { it.copy(emailError = "Invalid email format") }
            isValid = false
        }

        if (phone.isBlank()) {
            _state.update { it.copy(phoneError = "Please enter your phone number") }
            isValid = false
        } else if (phone.length < 11) {
            _state.update { it.copy(phoneError = "Phone number must be at least 11 digits") }
            isValid = false
        }

        if (pass.isBlank()) {
            _state.update { it.copy(passwordError = "Please create a password") }
            isValid = false
        } else if (pass.length < 6) {
            _state.update { it.copy(passwordError = "Password must be at least 6 characters") }
            isValid = false
        }

        return isValid
    }
}
