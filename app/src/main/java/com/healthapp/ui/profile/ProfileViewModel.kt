package com.healthapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthapp.data.local.entity.UserEntity
import com.healthapp.data.repository.UserRepository
import com.healthapp.util.BmiUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: UserEntity? = null,
    val isEditing: Boolean = false,
    val editName: String = "",
    val editGender: String = "",
    val editHeight: String = "",
    val editWeight: String = "",
    val editOccupation: String = "",
    val bmi: Float? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        userRepository.getCurrentUser().onEach { user ->
            val bmi = if (user != null) BmiUtils.calculateBmi(user.weight, user.height) else null

            _uiState.update {
                it.copy(
                    user = user,
                    bmi = bmi,
                    editName = user?.name ?: "",
                    editGender = user?.gender ?: "",
                    editHeight = user?.height?.toString() ?: "",
                    editWeight = user?.weight?.toString() ?: "",
                    editOccupation = user?.occupation ?: ""
                )
            }
        }.launchIn(viewModelScope)
    }

    fun startEditing() {
        val user = _uiState.value.user
        _uiState.update {
            it.copy(
                isEditing = true,
                editName = user?.name ?: "",
                editGender = user?.gender ?: "",
                editHeight = user?.height?.toString() ?: "",
                editWeight = user?.weight?.toString() ?: "",
                editOccupation = user?.occupation ?: ""
            )
        }
    }

    fun cancelEditing() {
        _uiState.update { it.copy(isEditing = false) }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(editName = name) }
    }

    fun updateGender(gender: String) {
        _uiState.update { it.copy(editGender = gender) }
    }

    fun updateHeight(height: String) {
        _uiState.update { it.copy(editHeight = height) }
    }

    fun updateWeight(weight: String) {
        _uiState.update { it.copy(editWeight = weight) }
    }

    fun updateOccupation(occupation: String) {
        _uiState.update { it.copy(editOccupation = occupation) }
    }

    fun saveProfile() {
        val state = _uiState.value
        val user = state.user

        val newUser = (user ?: UserEntity()).copy(
            name = state.editName,
            gender = state.editGender,
            height = state.editHeight.toFloatOrNull() ?: 0f,
            weight = state.editWeight.toFloatOrNull() ?: 0f,
            occupation = state.editOccupation,
            updatedAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            if (user == null) {
                userRepository.insertUser(newUser)
            } else {
                userRepository.updateUser(newUser)
            }
            _uiState.update { it.copy(isEditing = false) }
        }
    }
}
