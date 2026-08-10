package com.example.skillflow.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.analytics.AnalyticsHelper
import com.example.skillflow.domain.model.QuizQuestion
import com.example.skillflow.domain.repository.SettingsRepository
import com.example.skillflow.domain.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOption: Int? = null,
    val showFeedback: Boolean = false,
    val score: Int = 0,
    val isFinished: Boolean = false,
    val userAnswers: List<Int?> = emptyList(),
    val isLoading: Boolean = false
)

sealed class QuizUiEvent {
    object RequestReview : QuizUiEvent()
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val skillRepository: SkillRepository,
    private val settingsRepository: SettingsRepository,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {

    private val _state = MutableStateFlow(QuizState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<QuizUiEvent>()
    val events = _events.asSharedFlow()

    init {
        loadQuiz()
    }

    private fun loadQuiz() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val careerPathId = settingsRepository.getSelectedCareerPath().first()
            skillRepository.getQuizQuestions(careerPathId ?: "android").collect { questions ->
                _state.update { 
                    it.copy(
                        questions = questions, 
                        userAnswers = List(questions.size) { null },
                        isLoading = false
                    ) 
                }
            }
        }
    }

    fun onOptionSelected(index: Int) {
        if (!_state.value.showFeedback) {
            _state.update { it.copy(selectedOption = index) }
        }
    }

    fun submitAnswer() {
        val currentState = _state.value
        val isCorrect = currentState.selectedOption == currentState.questions[currentState.currentIndex].correctAnswerIndex
        val newScore = if (isCorrect) currentState.score + 1 else currentState.score
        
        val newUserAnswers = currentState.userAnswers.toMutableList()
        newUserAnswers[currentState.currentIndex] = currentState.selectedOption
        
        _state.update { 
            it.copy(
                showFeedback = true, 
                score = newScore,
                userAnswers = newUserAnswers
            ) 
        }
    }

    fun nextQuestion() {
        val currentState = _state.value
        if (currentState.currentIndex < currentState.questions.size - 1) {
            _state.update { 
                it.copy(
                    currentIndex = currentState.currentIndex + 1,
                    selectedOption = null,
                    showFeedback = false
                ) 
            }
        } else {
            _state.update { it.copy(isFinished = true) }
            saveQuizResults()
        }
    }

    private fun saveQuizResults() {
        viewModelScope.launch {
            settingsRepository.incrementQuizCount()
            settingsRepository.addToTotalQuizScore(_state.value.score)
            
            // Trigger review if score is good (e.g. > 70%)
            val percentage = if (_state.value.questions.isNotEmpty()) {
                (_state.value.score.toFloat() / _state.value.questions.size) * 100
            } else 0f
            
            if (percentage >= 70) {
                _events.emit(QuizUiEvent.RequestReview)
            }
            analyticsHelper.logQuizFinished(_state.value.score, _state.value.questions.size)
        }
    }
}
