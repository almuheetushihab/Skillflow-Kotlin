package com.example.skillflow.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.model.CareerPath
import com.example.skillflow.domain.repository.SettingsRepository
import com.example.skillflow.domain.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingState(
    val careerPaths: List<CareerPath> = emptyList(),
    val selectedCareerPathId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isOnboardingCompleted: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val skillRepository: SkillRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state = _state.asStateFlow()

    init {
        loadCareerPaths()
        checkOnboardingStatus()
    }

    private fun loadCareerPaths() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            skillRepository.getCareerPaths()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { paths ->
                    _state.update { it.copy(isLoading = false, careerPaths = paths) }
                }
        }
    }

    private fun checkOnboardingStatus() {
        viewModelScope.launch {
            settingsRepository.isOnboardingCompleted().collect { completed ->
                _state.update { it.copy(isOnboardingCompleted = completed) }
            }
        }
    }

    fun selectCareerPath(id: String) {
        _state.update { it.copy(selectedCareerPathId = id) }
    }

    fun completeOnboarding() {
        val selectedId = _state.value.selectedCareerPathId
        if (selectedId != null) {
            viewModelScope.launch {
                settingsRepository.saveSelectedCareerPath(selectedId)
                settingsRepository.setOnboardingCompleted(true)
            }
        } else {
            _state.update { it.copy(error = "Please select a career goal") }
        }
    }
}
