package com.example.skillflow.ui.screens.scanner

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.domain.repository.GeminiRepository
import com.example.skillflow.domain.repository.SettingsRepository
import com.example.skillflow.domain.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourseOption(
    val id: String,
    val name: String,
    val description: String
)

data class LectureScannerState(
    val selectedCourseId: String = "mentors_spoken",
    val selectedBitmap: Bitmap? = null,
    val isScanning: Boolean = false,
    val isSaving: Boolean = false,
    val userApiKey: String = "",
    val apiKeyInput: String = "",
    val scannedNugget: KnowledgeNugget? = null,
    val error: String? = null,
    val isSaved: Boolean = false,
    val availableCourses: List<CourseOption> = listOf(
        CourseOption("mentors_spoken", "Mentors' Spoken & Phonetics", "Classwork, Homework, Tongue Twisters, Phonetics & Fluency"),
        CourseOption("mentors_grammar", "Mentors' Foundation Grammar & Writing", "Grammar rules, Common mistakes, Reading & Writing tasks"),
        CourseOption("ielts", "IELTS Academic & General Prep", "4-Skills Masterclass: Listening, Reading, Writing & Speaking")
    )
)

sealed interface LectureScannerEvent {
    data class NavigateToDetail(val nuggetId: String) : LectureScannerEvent
    data class ShowSnackbar(val message: String) : LectureScannerEvent
}

@HiltViewModel
class LectureScannerViewModel @Inject constructor(
    private val geminiRepository: GeminiRepository,
    private val skillRepository: SkillRepository,
    private val settingsRepository: SettingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialCourseId: String = savedStateHandle.get<String>("targetCourseId") ?: "mentors_spoken"

    private val _state = MutableStateFlow(LectureScannerState(selectedCourseId = initialCourseId))
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<LectureScannerEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getGeminiApiKey().collect { key ->
                _state.update { it.copy(userApiKey = key, apiKeyInput = key) }
            }
        }
    }

    fun onCourseSelected(courseId: String) {
        _state.update { it.copy(selectedCourseId = courseId) }
    }

    fun onApiKeyInputChange(input: String) {
        _state.update { it.copy(apiKeyInput = input) }
    }

    fun saveApiKey() {
        val input = _state.value.apiKeyInput.trim()
        viewModelScope.launch {
            settingsRepository.saveGeminiApiKey(input)
            _state.update { it.copy(userApiKey = input, error = null) }
            _state.value.selectedBitmap?.let { bitmap ->
                analyzeSheet(bitmap)
            }
        }
    }

    fun onBitmapSelected(bitmap: Bitmap) {
        _state.update { it.copy(selectedBitmap = bitmap, scannedNugget = null, error = null) }
        analyzeSheet(bitmap)
    }

    fun analyzeSheet(bitmap: Bitmap? = _state.value.selectedBitmap) {
        val targetBitmap = bitmap ?: return
        _state.update { it.copy(isScanning = true, error = null) }

        viewModelScope.launch {
            geminiRepository.analyzeLectureSheetImage(targetBitmap, _state.value.selectedCourseId, _state.value.userApiKey)
                .catch { e ->
                    _state.update { it.copy(isScanning = false, error = e.localizedMessage ?: "Scanning failed") }
                }
                .collect { result ->
                    result.onSuccess { nugget ->
                        _state.update { it.copy(isScanning = false, scannedNugget = nugget, error = null) }
                    }.onFailure { err ->
                        _state.update { it.copy(isScanning = false, error = err.localizedMessage ?: "Failed to extract sheet content") }
                    }
                }
        }
    }

    fun useOfflineExtractor() {
        val targetBitmap = _state.value.selectedBitmap ?: return
        val offlineNugget = geminiRepository.extractLectureSheetOffline(targetBitmap, _state.value.selectedCourseId)
        _state.update { it.copy(isScanning = false, scannedNugget = offlineNugget, error = null) }
    }

    fun saveNuggetAndNavigate() {
        val nugget = _state.value.scannedNugget ?: return
        if (_state.value.isSaving) return

        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {
                skillRepository.insertCustomNugget(nugget)
                _state.update { it.copy(isSaved = true, isSaving = false) }
                _eventFlow.emit(LectureScannerEvent.NavigateToDetail(nugget.id))
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = "Failed to save nugget: ${e.localizedMessage}") }
            }
        }
    }

    fun resetState() {
        _state.update { it.copy(selectedBitmap = null, scannedNugget = null, error = null, isScanning = false, isSaving = false) }
    }
}
