package com.example.skillflow.ui.features.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.analytics.AnalyticsHelper
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.domain.model.UserNote
import com.example.skillflow.domain.repository.GeminiRepository
import com.example.skillflow.domain.repository.SettingsRepository
import com.example.skillflow.domain.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class DetailState(
    val nugget: KnowledgeNugget? = null,
    val noteTitle: String = "",
    val noteDescription: String = "",
    val notes: List<UserNote> = emptyList(),
    val editingNoteId: String? = null,
    val isFlipped: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    // AI Learning Assistant state
    val isAiBottomSheetOpen: Boolean = false,
    val aiQuestionInput: String = "",
    val aiMessages: List<ChatMessage> = listOf(
        ChatMessage(
            text = "Hello! I'm your SkillFlow AI Tutor. Ask me any question about this knowledge nugget, and I'll help you master it!",
            isFromUser = false
        )
    ),
    val isAiLoading: Boolean = false,
    val aiError: String? = null
)

sealed class DetailUiEvent {
    data class ShowSnackbar(val message: String) : DetailUiEvent()
    object ScrollToTop : DetailUiEvent()
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: SkillRepository,
    private val settingsRepository: SettingsRepository,
    private val geminiRepository: GeminiRepository,
    private val analyticsHelper: AnalyticsHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val nuggetId: String = checkNotNull(savedStateHandle["nuggetId"])

    private val _state = MutableStateFlow(DetailState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<DetailUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        loadNuggetAndNotes()
    }

    private fun loadNuggetAndNotes() {
        _state.update { it.copy(isLoading = true) }
        
        repository.getNuggetById(nuggetId)
            .onEach { nugget ->
                _state.update { it.copy(isLoading = false, nugget = nugget) }
            }
            .launchIn(viewModelScope)
        
        repository.getNotesForNugget(nuggetId)
            .onEach { notes ->
                _state.update { it.copy(notes = notes) }
            }
            .launchIn(viewModelScope)
    }

    fun flipCard() {
        _state.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun toggleMastered() {
        val currentMastered = _state.value.nugget?.isMastered ?: false
        viewModelScope.launch {
            repository.updateMasteryStatus(nuggetId, !currentMastered)
            val msg = if (!currentMastered) "Nugget Mastered! XP Gained." else "Mastery removed."
            _eventFlow.emit(DetailUiEvent.ShowSnackbar(msg))
        }
    }

    fun onNoteTitleChange(title: String) {
        _state.update { it.copy(noteTitle = title) }
    }

    fun onNoteDescriptionChange(description: String) {
        _state.update { it.copy(noteDescription = description) }
    }

    fun onEditNote(note: UserNote) {
        _state.update { 
            it.copy(
                noteTitle = note.title,
                noteDescription = note.noteContent,
                editingNoteId = note.id
            ) 
        }
    }

    fun saveNote() {
        val currentState = _state.value
        if (currentState.noteTitle.isBlank() || currentState.noteDescription.isBlank()) return

        viewModelScope.launch {
            try {
                val note = UserNote(
                    id = currentState.editingNoteId ?: UUID.randomUUID().toString(),
                    nuggetId = nuggetId,
                    title = currentState.noteTitle,
                    noteContent = currentState.noteDescription,
                    timestamp = System.currentTimeMillis()
                )
                repository.saveNote(note)
                
                _state.update { it.copy(noteTitle = "", noteDescription = "", editingNoteId = null) }
                _eventFlow.emit(DetailUiEvent.ShowSnackbar("Note saved!"))
                _eventFlow.emit(DetailUiEvent.ScrollToTop)
            } catch (e: Exception) {
                _eventFlow.emit(DetailUiEvent.ShowSnackbar("Error saving note"))
            }
        }
    }

    fun deleteNote(note: UserNote) {
        viewModelScope.launch {
            repository.deleteNote(note)
            _eventFlow.emit(DetailUiEvent.ShowSnackbar("Note deleted"))
        }
    }

    fun toggleSave() {
        viewModelScope.launch { repository.toggleSaveNugget(nuggetId) }
    }

    fun trackLearningTime(minutes: Long) {
        viewModelScope.launch { settingsRepository.addLearningTime(minutes) }
    }

    // AI Assistant functions
    fun toggleAiBottomSheet(isOpen: Boolean) {
        _state.update { it.copy(isAiBottomSheetOpen = isOpen) }
    }

    fun onAiQuestionChange(question: String) {
        _state.update { it.copy(aiQuestionInput = question) }
    }

    fun askAI(question: String = _state.value.aiQuestionInput) {
        val trimmedQuestion = question.trim()
        if (trimmedQuestion.isBlank()) return

        val currentNugget = _state.value.nugget
        val nuggetContext = if (currentNugget != null) {
            "Title: ${currentNugget.title}\nShort Description: ${currentNugget.shortDescription}\nContent: ${currentNugget.content}"
        } else {
            "Android Development & UI/UX"
        }

        val userMessage = ChatMessage(text = trimmedQuestion, isFromUser = true)

        _state.update {
            it.copy(
                aiMessages = it.aiMessages + userMessage,
                aiQuestionInput = "",
                isAiLoading = true,
                aiError = null
            )
        }

        viewModelScope.launch {
            geminiRepository.askGemini(context = nuggetContext, userQuestion = trimmedQuestion)
                .collect { result ->
                    result.onSuccess { responseText ->
                        val aiResponse = ChatMessage(text = responseText, isFromUser = false)
                        _state.update {
                            it.copy(
                                aiMessages = it.aiMessages + aiResponse,
                                isAiLoading = false,
                                aiError = null
                            )
                        }
                    }.onFailure { error ->
                        val errorMessage = error.localizedMessage ?: "Failed to get response from Gemini."
                        val aiErrorResponse = ChatMessage(
                            text = "Sorry, I ran into an error: $errorMessage",
                            isFromUser = false
                        )
                        _state.update {
                            it.copy(
                                aiMessages = it.aiMessages + aiErrorResponse,
                                isAiLoading = false,
                                aiError = errorMessage
                            )
                        }
                    }
                }
        }
    }
}
