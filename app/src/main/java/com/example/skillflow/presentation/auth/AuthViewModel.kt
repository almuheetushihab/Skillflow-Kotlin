package com.example.skillflow.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.repository.AuthRepository
import com.example.skillflow.domain.repository.SettingsRepository
import com.example.skillflow.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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

sealed class AuthUiEvent {
    data class ShowSnackbar(val message: String) : AuthUiEvent()
    object NavigateToOnboarding : AuthUiEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AuthUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

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

        viewModelScope.launch {
            authRepository.login(email, pass).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val userEmail = authRepository.getCurrentUserEmail() ?: email
                        val userName = authRepository.getCurrentUserName() 
                            ?: userEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
                            
                        settingsRepository.setLoggedIn(true)
                        settingsRepository.setUserEmail(userEmail)
                        settingsRepository.setUserName(userName)
                        
                        _state.update { it.copy(isLoading = false, isLoggedIn = true) }
                        _eventFlow.emit(AuthUiEvent.NavigateToOnboarding)
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                        _eventFlow.emit(AuthUiEvent.ShowSnackbar(result.message ?: "Login failed"))
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                }
            }
        }
    }

    fun signUp(name: String, email: String, phone: String, pass: String) {
        if (!validateSignUp(name, email, phone, pass)) return

        viewModelScope.launch {
            authRepository.signUp(name, email, phone, pass).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        settingsRepository.setLoggedIn(true)
                        settingsRepository.setUserName(name)
                        settingsRepository.setUserEmail(email)
                        _state.update { it.copy(isLoading = false, isLoggedIn = true) }
                        _eventFlow.emit(AuthUiEvent.NavigateToOnboarding)
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                        _eventFlow.emit(AuthUiEvent.ShowSnackbar(result.message ?: "Signup failed"))
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                }
            }
        }
    }

    private fun validateLogin(email: String, pass: String): Boolean {
        var isValid = true
        _state.update { it.copy(emailError = null, passwordError = null) }

        if (email.isBlank()) {
            _state.update { it.copy(emailError = "Please enter your email") }
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.update { it.copy(emailError = "Invalid email format") }
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
