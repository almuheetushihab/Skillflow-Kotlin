package com.example.skillflow.ui.screens.scanner

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.domain.repository.GeminiRepository
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialCourseId: String = savedStateHandle.get<String>("targetCourseId") ?: "mentors_spoken"

    private val _state = MutableStateFlow(LectureScannerState(selectedCourseId = initialCourseId))
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<LectureScannerEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onCourseSelected(courseId: String) {
        _state.update { it.copy(selectedCourseId = courseId) }
    }

    fun onBitmapSelected(bitmap: Bitmap) {
        _state.update { it.copy(selectedBitmap = bitmap, scannedNugget = null, error = null) }
        analyzeSheet(bitmap)
    }

    fun analyzeSheet(bitmap: Bitmap? = _state.value.selectedBitmap) {
        val targetBitmap = bitmap ?: return
        _state.update { it.copy(isScanning = true, error = null) }

        viewModelScope.launch {
            geminiRepository.analyzeLectureSheetImage(targetBitmap, _state.value.selectedCourseId)
                .catch { e ->
                    _state.update { it.copy(isScanning = false, error = e.localizedMessage ?: "Scanning failed") }
                }
                .collect { result ->
                    result.onSuccess { nugget ->
                        _state.update { it.copy(isScanning = false, scannedNugget = nugget) }
                    }.onFailure { err ->
                        _state.update { it.copy(isScanning = false, error = err.localizedMessage ?: "Failed to extract sheet content") }
                    }
                }
        }
    }

    fun saveNuggetAndNavigate() {
        val nugget = _state.value.scannedNugget ?: return
        viewModelScope.launch {
            skillRepository.insertCustomNugget(nugget)
            _state.update { it.copy(isSaved = true) }
            _eventFlow.emit(LectureScannerEvent.NavigateToDetail(nugget.id))
        }
    }

    fun resetState() {
        _state.update { it.copy(selectedBitmap = null, scannedNugget = null, error = null, isScanning = false) }
    }
}
