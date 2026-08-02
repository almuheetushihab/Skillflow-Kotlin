package com.example.skillflow.presentation.quiz

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.R
import com.example.skillflow.domain.repository.SettingsRepository
import com.example.skillflow.domain.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizQuestion(
    val id: String,
    @StringRes val textRes: Int,
    val optionsRes: List<Int>,
    val correctAnswerIndex: Int,
    @StringRes val explanationRes: Int
)

data class QuizState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOption: Int? = null,
    val showFeedback: Boolean = false,
    val score: Int = 0,
    val isFinished: Boolean = false,
    val userAnswers: List<Int?> = emptyList()
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val skillRepository: SkillRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QuizState())
    val state = _state.asStateFlow()

    init {
        loadQuiz()
    }

    private fun loadQuiz() {
        val questions = listOf(
            QuizQuestion("1", R.string.q1_text, listOf(R.string.q1_o1, R.string.q1_o2, R.string.q1_o3), 1, R.string.q1_exp),
            QuizQuestion("2", R.string.q2_text, listOf(R.string.q2_o1, R.string.q2_o2, R.string.q2_o3), 1, R.string.q2_exp),
            QuizQuestion("3", R.string.q3_text, listOf(R.string.q3_o1, R.string.q3_o2, R.string.q3_o3), 2, R.string.q3_exp),
            QuizQuestion("4", R.string.q4_text, listOf(R.string.q4_o1, R.string.q4_o2, R.string.q4_o3), 1, R.string.q4_exp),
            QuizQuestion("5", R.string.q5_text, listOf(R.string.q5_o1, R.string.q5_o2, R.string.q5_o3), 2, R.string.q5_exp),
            QuizQuestion("6", R.string.q6_text, listOf(R.string.q6_o1, R.string.q6_o2, R.string.q6_o3), 0, R.string.q6_exp),
            QuizQuestion("7", R.string.q7_text, listOf(R.string.q7_o1, R.string.q7_o2, R.string.q7_o3), 1, R.string.q7_exp),
            QuizQuestion("8", R.string.q8_text, listOf(R.string.q8_o1, R.string.q8_o2, R.string.q8_o3), 2, R.string.q8_exp),
            QuizQuestion("9", R.string.q9_text, listOf(R.string.q9_o1, R.string.q9_o2, R.string.q9_o3), 1, R.string.q9_exp),
            QuizQuestion("10", R.string.q10_text, listOf(R.string.q10_o1, R.string.q10_o2, R.string.q10_o3), 0, R.string.q10_exp),
            QuizQuestion("11", R.string.q11_text, listOf(R.string.q11_o1, R.string.q11_o2, R.string.q11_o3), 1, R.string.q11_exp),
            QuizQuestion("12", R.string.q12_text, listOf(R.string.q12_o1, R.string.q12_o2, R.string.q12_o3), 0, R.string.q12_exp),
            QuizQuestion("13", R.string.q13_text, listOf(R.string.q13_o1, R.string.q13_o2, R.string.q13_o3), 1, R.string.q13_exp),
            QuizQuestion("14", R.string.q14_text, listOf(R.string.q14_o1, R.string.q14_o2, R.string.q14_o3), 1, R.string.q14_exp),
            QuizQuestion("15", R.string.q15_text, listOf(R.string.q15_o1, R.string.q15_o2, R.string.q15_o3), 1, R.string.q15_exp),
            QuizQuestion("16", R.string.q16_text, listOf(R.string.q16_o1, R.string.q16_o2, R.string.q16_o3), 1, R.string.q16_exp),
            QuizQuestion("17", R.string.q17_text, listOf(R.string.q17_o1, R.string.q17_o2, R.string.q17_o3), 2, R.string.q17_exp),
            QuizQuestion("18", R.string.q18_text, listOf(R.string.q18_o1, R.string.q18_o2, R.string.q18_o3), 2, R.string.q18_exp),
            QuizQuestion("19", R.string.q19_text, listOf(R.string.q19_o1, R.string.q19_o2, R.string.q19_o3), 0, R.string.q19_exp),
            QuizQuestion("20", R.string.q20_text, listOf(R.string.q20_o1, R.string.q20_o2, R.string.q20_o3), 1, R.string.q20_exp)
        )
        _state.update { it.copy(questions = questions, userAnswers = List(questions.size) { null }) }
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
        }
    }
}
