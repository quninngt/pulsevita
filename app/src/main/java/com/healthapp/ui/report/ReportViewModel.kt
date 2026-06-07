package com.healthapp.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthapp.data.remote.HealthReport
import com.healthapp.data.remote.NetworkResult
import com.healthapp.data.repository.ServerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportUiState(
    val isLoading: Boolean = false,
    val currentReport: HealthReport? = null,
    val reportList: List<HealthReport> = emptyList(),
    val selectedType: String = "weekly",
    val error: String? = null
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        loadLatestReport("weekly")
    }

    fun loadLatestReport(type: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, selectedType = type) }

            when (val result = serverRepository.getLatestReport(type)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            currentReport = result.data,
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

    fun loadReportList(type: String, page: Int = 1) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, selectedType = type) }

            when (val result = serverRepository.getReportList(type, page)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            reportList = result.data.reports,
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

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
