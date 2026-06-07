package com.healthapp.ui.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthapp.data.remote.NetworkResult
import com.healthapp.data.remote.PlanItem
import com.healthapp.data.repository.ServerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlanUiState(
    val isLoading: Boolean = false,
    val activePlans: List<PlanItem> = emptyList(),
    val completedPlans: List<PlanItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanUiState())
    val uiState: StateFlow<PlanUiState> = _uiState.asStateFlow()

    init {
        loadPlans()
    }

    fun loadPlans() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = serverRepository.getAllPlans()) {
                is NetworkResult.Success -> {
                    val plans = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            activePlans = plans.filter { plan -> !plan.completed },
                            completedPlans = plans.filter { plan -> plan.completed },
                            error = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
                NetworkResult.Loading -> { /* handled above */ }
            }
        }
    }

    fun updateProgress(planId: Long, progress: Int) {
        viewModelScope.launch {
            when (val result = serverRepository.updateProgress(planId, progress)) {
                is NetworkResult.Success -> {
                    loadPlans()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
                NetworkResult.Loading -> { /* no-op */ }
            }
        }
    }

    fun completePlan(planId: Long) {
        viewModelScope.launch {
            when (val result = serverRepository.completePlan(planId)) {
                is NetworkResult.Success -> {
                    loadPlans()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
                NetworkResult.Loading -> { /* no-op */ }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
