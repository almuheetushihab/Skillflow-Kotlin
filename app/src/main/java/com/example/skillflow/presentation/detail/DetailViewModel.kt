package com.example.skillflow.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.domain.repository.SettingsRepository
import com.example.skillflow.domain.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailState(
    val nugget: KnowledgeNugget? = null,
    val isFlipped: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: SkillRepository,
    private val settingsRepository: SettingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val nuggetId: String = checkNotNull(savedStateHandle["nuggetId"])

    private val _state = MutableStateFlow(DetailState())
    val state = _state.asStateFlow()

    init {
        loadNugget()
    }

    private fun loadNugget() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.getDailyNuggets("") 
                .collect { nuggets ->
                    val nugget = nuggets.find { it.id == nuggetId }
                    if (nugget != null) {
                        _state.update { it.copy(isLoading = false, nugget = nugget) }
                    } else {
                        repository.getSavedNuggets().collect { saved ->
                            val savedNugget = saved.find { it.id == nuggetId }
                            _state.update { it.copy(isLoading = false, nugget = savedNugget) }
                        }
                    }
                }
        }
    }

    fun flipCard() {
        _state.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun markAsDone() {
        viewModelScope.launch {
            repository.markNuggetAsDone(nuggetId)
            _state.update { it.copy(nugget = it.nugget?.copy(isDone = true)) }
        }
    }

    fun toggleSave() {
        viewModelScope.launch {
            repository.toggleSaveNugget(nuggetId)
            _state.update { it.copy(nugget = it.nugget?.copy(isSaved = !it.nugget.isSaved)) }
        }
    }

    fun trackLearningTime(minutes: Long) {
        viewModelScope.launch {
            settingsRepository.addLearningTime(minutes)
        }
    }
}
