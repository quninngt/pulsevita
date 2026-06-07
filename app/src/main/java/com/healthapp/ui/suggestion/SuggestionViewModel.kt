package com.healthapp.ui.suggestion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthapp.data.remote.DailySuggestionResponse
import com.healthapp.data.remote.NetworkResult
import com.healthapp.data.remote.SuggestionData
import com.healthapp.data.repository.ServerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class SuggestionUiState(
    val isLoading: Boolean = false,
    val suggestions: List<SuggestionData> = emptyList(),
    val dailySuggestionId: Long = 0,
    val userVotedId: Long? = null,
    val error: String? = null
)

@HiltViewModel
class SuggestionViewModel @Inject constructor(
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SuggestionUiState())
    val uiState: StateFlow<SuggestionUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        loadDailySuggestions()
    }

    fun loadDailySuggestions(date: String = LocalDate.now().format(dateFormatter)) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = serverRepository.getDailySuggestions(date)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            suggestions = result.data.suggestions,
                            dailySuggestionId = result.data.id,
                            userVotedId = result.data.userVotedSuggestionId,
                            error = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                NetworkResult.Loading -> { /* handled above */ }
            }
        }
    }

    fun vote(dailySuggestionId: Long, suggestionId: Long) {
        viewModelScope.launch {
            when (val result = serverRepository.vote(dailySuggestionId, suggestionId)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(userVotedId = suggestionId) }
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
