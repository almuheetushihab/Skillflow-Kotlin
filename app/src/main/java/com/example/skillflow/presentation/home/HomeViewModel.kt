package com.example.skillflow.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.domain.repository.SettingsRepository
import com.example.skillflow.domain.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class HomeState(
    val dailyNuggets: List<KnowledgeNugget> = emptyList(),
    val streakCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val careerPathId: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val skillRepository: SkillRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            settingsRepository.getSelectedCareerPath().collect { id ->
                if (id != null) {
                    _state.update { it.copy(careerPathId = id) }
                    loadNuggets(id)
                }
            }
        }
        viewModelScope.launch {
            settingsRepository.getStreakCount().collect { streak ->
                _state.update { it.copy(streakCount = streak) }
            }
        }
    }

    private fun loadNuggets(careerPathId: String) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            skillRepository.getDailyNuggets(careerPathId)
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { nuggets ->
                    _state.update { it.copy(isLoading = false, dailyNuggets = nuggets) }
                    checkAndUpdateStreak()
                }
        }
    }

    private fun checkAndUpdateStreak() {
        val today = dateFormatter.format(Date())
        viewModelScope.launch {
            val lastActivityDate = settingsRepository.getLastActivityDate().first()
            if (lastActivityDate != today) {
                val currentStreak = settingsRepository.getStreakCount().first()
                // Simple logic: if last activity was yesterday, increment. If older, reset.
                // For now, just increment if it's a new day activity
                settingsRepository.updateStreak(currentStreak + 1)
                settingsRepository.updateLastActivityDate(today)
            }
        }
    }
}
