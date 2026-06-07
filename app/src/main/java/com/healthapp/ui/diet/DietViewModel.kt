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
    val dietCalories: String = "",
    // === 新增：营养数据 ===
    val protein: Float = 0f,
    val proteinGoal: Float = 60f, // 默认目标60g
    val carbs: Float = 0f,
    val carbsGoal: Float = 300f, // 默认目标300g
    val fat: Float = 0f,
    val fatGoal: Float = 65f, // 默认目标65g
    // === 新增：饮水历史数据 ===
    val weeklyWaterAmounts: List<Int> = emptyList()
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
            calculateNutrition(records)
        }.launchIn(viewModelScope)

        // 加载饮水历史数据
        loadWeeklyWaterData()
    }

    /**
     * 加载近7天饮水数据
     */
    private fun loadWeeklyWaterData() {
        viewModelScope.launch {
            val weeklyAmounts = waterRepository.getLast7DaysAmounts()
            _uiState.update { it.copy(weeklyWaterAmounts = weeklyAmounts) }
        }
    }

    /**
     * 计算营养摄入
     */
    private fun calculateNutrition(records: List<DietRecord>) {
        var totalProtein = 0f
        var totalCarbs = 0f
        var totalFat = 0f

        records.forEach { record ->
            // 简单估算：根据卡路里估算营养成分
            // 实际应用中应该从食物数据库获取
            val calories = record.calories?.toFloat() ?: 0f
            totalProtein += calories * 0.3f / 4f // 蛋白质占30%，1g=4卡
            totalCarbs += calories * 0.5f / 4f   // 碳水占50%，1g=4卡
            totalFat += calories * 0.2f / 9f     // 脂肪占20%，1g=9卡
        }

        _uiState.update {
            it.copy(
                protein = totalProtein,
                carbs = totalCarbs,
                fat = totalFat
            )
        }
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
