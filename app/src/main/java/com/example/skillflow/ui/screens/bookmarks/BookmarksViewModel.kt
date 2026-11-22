package com.example.skillflow.ui.screens.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.domain.repository.SettingsRepository
import com.example.skillflow.domain.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookmarksState(
    val savedNuggets: List<KnowledgeNugget> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val repository: SkillRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BookmarksState())
    val state = _state.asStateFlow()

    init {
        loadBookmarks()
    }

    private fun loadBookmarks() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            settingsRepository.getSelectedCareerPath()
                .filterNotNull()
                .flatMapLatest { pathId ->
                    repository.getSavedNuggetsByPath(pathId)
                }
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { nuggets ->
                    _state.update { it.copy(isLoading = false, savedNuggets = nuggets) }
                }
        }
    }
}
