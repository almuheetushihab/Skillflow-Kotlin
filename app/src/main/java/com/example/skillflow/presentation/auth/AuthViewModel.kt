package com.example.skillflow.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.R
import com.example.skillflow.domain.repository.AuthRepository
import com.example.skillflow.domain.repository.SettingsRepository
import com.example.skillflow.domain.util.Resource
import com.example.skillflow.domain.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val email: String = "",
    val phoneNumber: String = "",
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val isLoggedIn: Boolean = false,
    val isRememberMe: Boolean = false,
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val phoneError: UiText? = null,
    val nameError: UiText? = null
)

sealed class AuthUiEvent {
    data class ShowSnackbar(val message: UiText) : AuthUiEvent()
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
                        val message = result.message?.let { UiText.DynamicString(it) } 
                            ?: UiText.StringResource(R.string.login_failed)
                        _state.update { it.copy(isLoading = false, error = message) }
                        _eventFlow.emit(AuthUiEvent.ShowSnackbar(message))
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
                        val message = result.message?.let { UiText.DynamicString(it) } 
                            ?: UiText.StringResource(R.string.signup_failed)
                        _state.update { it.copy(isLoading = false, error = message) }
                        _eventFlow.emit(AuthUiEvent.ShowSnackbar(message))
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
            _state.update { it.copy(emailError = UiText.StringResource(R.string.error_email_empty)) }
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.update { it.copy(emailError = UiText.StringResource(R.string.error_invalid_email)) }
            isValid = false
        }

        if (pass.isBlank()) {
            _state.update { it.copy(passwordError = UiText.StringResource(R.string.error_password_empty)) }
            isValid = false
        }

        return isValid
    }

    private fun validateSignUp(name: String, email: String, phone: String, pass: String): Boolean {
        var isValid = true
        _state.update { it.copy(nameError = null, emailError = null, phoneError = null, passwordError = null) }

        if (name.isBlank()) {
            _state.update { it.copy(nameError = UiText.StringResource(R.string.error_name_empty)) }
            isValid = false
        }

        if (email.isBlank()) {
            _state.update { it.copy(emailError = UiText.StringResource(R.string.error_email_empty)) }
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.update { it.copy(emailError = UiText.StringResource(R.string.error_invalid_email)) }
            isValid = false
        }

        if (phone.isBlank()) {
            _state.update { it.copy(phoneError = UiText.StringResource(R.string.error_phone_empty)) }
            isValid = false
        } else if (phone.length < 11) {
            _state.update { it.copy(phoneError = UiText.StringResource(R.string.error_phone_short)) }
            isValid = false
        }

        if (pass.isBlank()) {
            _state.update { it.copy(passwordError = UiText.StringResource(R.string.error_password_create)) }
            isValid = false
        } else if (pass.length < 6) {
            _state.update { it.copy(passwordError = UiText.StringResource(R.string.error_password_short)) }
            isValid = false
        }

        return isValid
    }
}
