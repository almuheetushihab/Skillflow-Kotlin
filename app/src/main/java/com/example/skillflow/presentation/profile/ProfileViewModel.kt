package com.example.skillflow.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.repository.SettingsRepository
import com.example.skillflow.domain.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class ProfileState(
    val userName: String = "",
    val userEmail: String = "",
    val profilePictureUri: String? = null,
    val careerPathId: String? = null,
    val streakCount: Int = 0,
    val isDarkMode: Boolean = false,
    val todayLearned: Int = 0,
    val todayTotal: Int = 0,
    val profileLevel: Int = 1,
    val learnedTopics: List<String> = emptyList(),
    val quizCount: Int = 0,
    val averageScore: Float = 0f
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val skillRepository: SkillRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val today = dateFormatter.format(Date())
        
        viewModelScope.launch {
            settingsRepository.getSelectedCareerPath().collect { id ->
                _state.update { it.copy(careerPathId = id) }
                if (id != null) {
                    loadStats(id, today)
                }
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
        viewModelScope.launch {
            settingsRepository.getUserEmail().collect { email ->
                _state.update { it.copy(userEmail = email ?: "") }
            }
        }
        viewModelScope.launch {
            combine(
                settingsRepository.getQuizCount(),
                settingsRepository.getTotalQuizScore()
            ) { count, totalScore ->
                count to totalScore
            }.collect { (count, totalScore) ->
                val avg = if (count > 0) totalScore.toFloat() / count else 0f
                _state.update { it.copy(quizCount = count, averageScore = avg) }
            }
        }
    }

    private fun loadStats(careerPathId: String, date: String) {
        viewModelScope.launch {
            skillRepository.getDailyProgress(careerPathId, date).collect { (learned, total) ->
                _state.update { it.copy(todayLearned = learned, todayTotal = total) }
            }
        }
        viewModelScope.launch {
            skillRepository.getRecentlyLearnedTopics(careerPathId).collect { topics ->
                _state.update { 
                    it.copy(
                        learnedTopics = topics,
                        profileLevel = (topics.size / 5) + 1 // Simple level logic
                    ) 
                }
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
