package com.healthapp.ui.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthapp.data.local.entity.DietRecord
import com.healthapp.data.local.entity.WaterRecord
import com.healthapp.data.repository.DietRepository
import com.healthapp.data.repository.WaterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DietUiState(
    val waterAmount: Int = 0,
    val waterGoal: Int = 2000,
    val waterRecords: List<WaterRecord> = emptyList(),
    val dietRecords: List<DietRecord> = emptyList(),
    val showAddDietDialog: Boolean = false,
    val selectedMealType: String = "breakfast",
    val dietDescription: String = "",
    val dietCalories: String = ""
)

@HiltViewModel
class DietViewModel @Inject constructor(
    private val waterRepository: WaterRepository,
    private val dietRepository: DietRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DietUiState())
    val uiState: StateFlow<DietUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        waterRepository.getTodayTotalAmount().onEach { amount ->
            _uiState.update { it.copy(waterAmount = amount) }
        }.launchIn(viewModelScope)

        waterRepository.getTodayRecords().onEach { records ->
            _uiState.update { it.copy(waterRecords = records) }
        }.launchIn(viewModelScope)

        dietRepository.getTodayRecords().onEach { records ->
            _uiState.update { it.copy(dietRecords = records) }
        }.launchIn(viewModelScope)
    }

    fun addWater(amount: Int) {
        viewModelScope.launch {
            waterRepository.addWater(amount)
        }
    }

    fun showAddDietDialog(mealType: String) {
        _uiState.update {
            it.copy(
                showAddDietDialog = true,
                selectedMealType = mealType
            )
        }
    }

    fun hideAddDietDialog() {
        _uiState.update {
            it.copy(
                showAddDietDialog = false,
                dietDescription = "",
                dietCalories = ""
            )
        }
    }

    fun updateDietDescription(description: String) {
        _uiState.update { it.copy(dietDescription = description) }
    }

    fun updateDietCalories(calories: String) {
        _uiState.update { it.copy(dietCalories = calories) }
    }

    fun saveDietRecord() {
        val state = _uiState.value
        if (state.dietDescription.isNotBlank()) {
            viewModelScope.launch {
                dietRepository.addDiet(
                    mealType = state.selectedMealType,
                    description = state.dietDescription,
                    calories = state.dietCalories.toIntOrNull()
                )
                hideAddDietDialog()
            }
        }
    }

    fun deleteWaterRecord(record: WaterRecord) {
        viewModelScope.launch {
            waterRepository.deleteRecord(record)
        }
    }

    fun deleteDietRecord(record: DietRecord) {
        viewModelScope.launch {
            dietRepository.deleteRecord(record)
        }
    }
}
