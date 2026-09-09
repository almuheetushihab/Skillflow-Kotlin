package com.example.skillflow.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.domain.repository.SettingsRepository
import com.example.skillflow.domain.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class HomeState(
    val dailyNuggets: List<KnowledgeNugget> = emptyList(),
    val streakCount: Int = 0,
    val savedCount: Int = 0,
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<KnowledgeNugget> = emptyList(),
    val isPathFinished: Boolean = false,
    val totalLearned: Int = 0,
    val totalCount: Int = 0,
    val selectedDate: String? = null,
    val availableDates: List<DateModel> = emptyList(),
    val isRefreshing: Boolean = false
)

data class DateModel(
    val dayName: String,
    val dateDisplay: String,
    val fullDate: String,
    val isSelected: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val skillRepository: SkillRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        generateDateStrip()
        loadHomeData()
        observeSearch()
    }

    private fun generateDateStrip() {
        val dates = mutableListOf<DateModel>()
        val calendar = Calendar.getInstance()
        
        for (i in 0..7) {
            val date = calendar.time
            dates.add(
                DateModel(
                    dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(date),
                    dateDisplay = SimpleDateFormat("dd", Locale.getDefault()).format(date),
                    fullDate = dateFormatter.format(date),
                    isSelected = false
                )
            )
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        _state.update { it.copy(availableDates = dates) }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun observeSearch() {
        _searchQuery
            .debounce(300)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (query.isBlank()) flowOf(emptyList()) else skillRepository.searchNuggets(query)
            }
            .onEach { results ->
                _state.update { it.copy(searchResults = results) }
            }
            .launchIn(viewModelScope)
    }

    fun loadHomeData() {
        _state.update { it.copy(isLoading = true) }
        
        settingsRepository.getStreakCount()
            .onEach { count -> _state.update { it.copy(streakCount = count) } }
            .launchIn(viewModelScope)

        skillRepository.getSavedNuggets()
            .onEach { savedList -> _state.update { it.copy(savedCount = savedList.size) } }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            settingsRepository.getSelectedCareerPath().filterNotNull().collectLatest { pathId ->
                skillRepository.getDailyProgress(pathId, "").collect { progress ->
                    _state.update { 
                        it.copy(
                            totalLearned = progress.first,
                            totalCount = progress.second,
                            isPathFinished = progress.second > 0 && progress.first == progress.second
                        ) 
                    }
                    if (progress.second == 0 && !_state.value.isRefreshing) {
                        skillRepository.seedDatabase()
                    }
                }
            }
        }

        viewModelScope.launch {
            combine(
                settingsRepository.getSelectedCareerPath().filterNotNull(),
                _state.map { it.selectedDate }.distinctUntilChanged()
            ) { pathId, date ->
                pathId to date
            }.collectLatest { (pathId, date) ->
                val nuggetsFlow = if (date == null) {
                    skillRepository.getDailyNuggets(pathId).map { list ->
                        list.sortedBy { it.priority }
                    }
                } else {
                    skillRepository.getNuggetsByDate(pathId, date)
                }

                nuggetsFlow.collect { nuggets ->
                    _state.update { it.copy(dailyNuggets = nuggets, isLoading = false, isRefreshing = false) }
                }
            }
        }
    }

    fun onDateSelected(date: String?) {
        _state.update { 
            it.copy(
                selectedDate = date,
                availableDates = it.availableDates.map { model ->
                    model.copy(isSelected = model.fullDate == date)
                }
            ) 
        }
    }

    fun refresh() {
        _state.update { it.copy(isRefreshing = true, selectedDate = null) }
        viewModelScope.launch {
            skillRepository.seedDatabase()
            loadHomeData()
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _state.update { it.copy(searchQuery = query, isSearching = query.isNotEmpty()) }
    }
}
