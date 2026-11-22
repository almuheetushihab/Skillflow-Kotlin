package com.example.skillflow.ui.screens.roadmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.domain.repository.SettingsRepository
import com.example.skillflow.domain.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class RoadmapStepUiModel(
    val id: String,
    val title: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

data class RoadmapState(
    val steps: List<RoadmapStepUiModel> = emptyList(),
    val careerPathName: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RoadmapViewModel @Inject constructor(
    private val skillRepository: SkillRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RoadmapState())
    val state = _state.asStateFlow()

    init {
        loadRoadmap()
    }

    fun loadRoadmap() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            combine(
                settingsRepository.getSelectedCareerPath().filterNotNull(),
                skillRepository.getCareerPaths()
            ) { pathId, careerPaths ->
                val pathName = careerPaths.find { it.id == pathId }?.name ?: ""
                pathId to pathName
            }.flatMapLatest { (pathId, pathName) ->
                _state.update { it.copy(careerPathName = pathName) }
                skillRepository.getDailyNuggets(pathId)
            }.catch { e ->
                _state.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Failed to load roadmap") }
            }.collect { nuggets ->
                if (nuggets.isEmpty()) {
                    skillRepository.seedDatabase()
                } else {
                    val firstUncompletedIndex = nuggets.indexOfFirst { !it.isMastered && !it.isDone }
                    val activeIndex = if (firstUncompletedIndex == -1) nuggets.size - 1 else firstUncompletedIndex

                    val stepModels = nuggets.mapIndexed { index, nugget ->
                        RoadmapStepUiModel(
                            id = nugget.id,
                            title = nugget.title,
                            isCompleted = nugget.isMastered || nugget.isDone,
                            isCurrent = index == activeIndex
                        )
                    }
                    _state.update {
                        it.copy(
                            steps = stepModels,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            }
        }
    }

    fun addCustomStep(
        title: String,
        shortDescription: String = "",
        content: String = "",
        complexity: String = "Intermediate"
    ) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) return

        viewModelScope.launch {
            val currentPathId = settingsRepository.getSelectedCareerPath().firstOrNull() ?: "android"
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val newNugget = KnowledgeNugget(
                id = "custom_${System.currentTimeMillis()}",
                title = trimmedTitle,
                shortDescription = shortDescription.ifBlank { "Custom topic added to roadmap." },
                content = content.ifBlank { "Study notes and key concepts for $trimmedTitle." },
                complexity = complexity,
                careerPathId = currentPathId,
                priority = state.value.steps.size + 1,
                date = today,
                isMastered = false,
                isDone = false,
                isSaved = false
            )
            skillRepository.insertCustomNugget(newNugget)
        }
    }
}
