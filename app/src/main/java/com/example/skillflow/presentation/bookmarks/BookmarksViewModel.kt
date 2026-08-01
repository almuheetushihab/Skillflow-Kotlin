package com.example.skillflow.presentation.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.domain.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookmarksState(
    val savedNuggets: List<KnowledgeNugget> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val repository: SkillRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BookmarksState())
    val state = _state.asStateFlow()

    init {
        loadBookmarks()
    }

    private fun loadBookmarks() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.getSavedNuggets()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { nuggets ->
                    _state.update { it.copy(isLoading = false, savedNuggets = nuggets) }
                }
        }
    }
}
