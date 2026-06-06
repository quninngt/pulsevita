package com.healthapp.ui.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthapp.data.local.entity.ExerciseRecord
import com.healthapp.data.repository.ExerciseRepository
import com.healthapp.util.HealthConnectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OfficeExercise(
    val title: String,
    val description: String,
    val duration: String
)

data class ExerciseUiState(
    val todayDuration: Int = 0,
    val durationGoal: Int = 30,
    val todaySteps: Long = 0L,
    val stepsGoal: Long = 8000L,
    val exerciseRecords: List<ExerciseRecord> = emptyList(),
    val showAddDialog: Boolean = false,
    val selectedType: String = "walking",
    val exerciseDuration: String = "",
    val exerciseSteps: String = "",
    val exerciseNote: String = "",
    val isHealthConnectAvailable: Boolean = false,
    val hasHealthConnectPermissions: Boolean = false,
    val officeExercises: List<OfficeExercise> = emptyList(),
    val weeklyMinutes: List<Int> = emptyList()
)

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val healthConnectManager: HealthConnectManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    init {
        checkHealthConnect()
        loadData()
        loadOfficeExercises()
    }

    private fun loadOfficeExercises() {
        _uiState.update { it.copy(officeExercises = listOf(
            OfficeExercise("颈部放松", "坐直，缓慢转动头部，顺时针5圈，逆时针5圈。缓解颈椎疲劳。", "2分钟"),
            OfficeExercise("肩部耸肩", "双肩向上耸起，保持5秒，然后放松。重复10次。缓解肩部紧张。", "3分钟"),
            OfficeExercise("腰部扭转", "坐在椅子上，双手叉腰，缓慢转动腰部。左右各10次。预防腰椎问题。", "3分钟"),
            OfficeExercise("手腕活动", "双手握拳，然后张开，重复10次。转动手腕，顺时针逆时针各10圈。", "2分钟")
        )) }
    }

    private fun checkHealthConnect() {
        val isAvailable = healthConnectManager.isHealthConnectAvailable()
        _uiState.update { it.copy(isHealthConnectAvailable = isAvailable) }

        if (isAvailable) {
            viewModelScope.launch {
                val hasPermissions = healthConnectManager.hasAllPermissions()
                _uiState.update { it.copy(hasHealthConnectPermissions = hasPermissions) }
            }
        }
    }

    private fun loadData() {
        // Load weekly exercise data
        viewModelScope.launch {
            val minutes = exerciseRepository.getLast7DaysMinutes()
            _uiState.update { it.copy(weeklyMinutes = minutes) }
        }

        // Load from local database
        exerciseRepository.getTodayTotalDuration().onEach { duration ->
            _uiState.update { it.copy(todayDuration = duration) }
        }.launchIn(viewModelScope)

        exerciseRepository.getTodayRecords().onEach { records ->
            _uiState.update { it.copy(exerciseRecords = records) }
        }.launchIn(viewModelScope)

        // Load steps from Health Connect if available
        if (_uiState.value.isHealthConnectAvailable && _uiState.value.hasHealthConnectPermissions) {
            loadStepsFromHealthConnect()
        } else {
            // Fallback to local database
            exerciseRepository.getTodayTotalSteps().onEach { steps ->
                _uiState.update { it.copy(todaySteps = steps.toLong()) }
            }.launchIn(viewModelScope)
        }
    }

    private fun loadStepsFromHealthConnect() {
        viewModelScope.launch {
            try {
                val steps = healthConnectManager.getTodaySteps()
                _uiState.update { it.copy(todaySteps = steps) }
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback to local data
                exerciseRepository.getTodayTotalSteps().first().let { steps ->
                    _uiState.update { it.copy(todaySteps = steps.toLong()) }
                }
            }
        }
    }

    fun refreshSteps() {
        if (_uiState.value.isHealthConnectAvailable && _uiState.value.hasHealthConnectPermissions) {
            loadStepsFromHealthConnect()
        }
    }

    fun getHealthConnectPermissions(): Set<String> {
        return healthConnectManager.permissions.map { it.toString() }.toSet()
    }

    fun showAddDialog(type: String) {
        _uiState.update {
            it.copy(
                showAddDialog = true,
                selectedType = type
            )
        }
    }

    fun hideAddDialog() {
        _uiState.update {
            it.copy(
                showAddDialog = false,
                exerciseDuration = "",
                exerciseSteps = "",
                exerciseNote = ""
            )
        }
    }

    fun updateDuration(duration: String) {
        _uiState.update { it.copy(exerciseDuration = duration) }
    }

    fun updateSteps(steps: String) {
        _uiState.update { it.copy(exerciseSteps = steps) }
    }

    fun updateNote(note: String) {
        _uiState.update { it.copy(exerciseNote = note) }
    }

    fun saveExerciseRecord() {
        val state = _uiState.value
        val duration = state.exerciseDuration.toIntOrNull()
        if (duration != null && duration > 0) {
            viewModelScope.launch {
                exerciseRepository.addExercise(
                    type = state.selectedType,
                    duration = duration,
                    steps = state.exerciseSteps.toIntOrNull(),
                    note = state.exerciseNote.ifBlank { null }
                )
                hideAddDialog()
            }
        }
    }

    fun deleteRecord(record: ExerciseRecord) {
        viewModelScope.launch {
            exerciseRepository.deleteRecord(record)
        }
    }
}
