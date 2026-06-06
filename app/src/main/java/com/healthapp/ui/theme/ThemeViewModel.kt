package com.healthapp.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 主题 ViewModel
 * 管理主题状态和切换逻辑
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val themeManager = ThemeManager(application)

    // 当前主题状态
    private val _currentScheme = MutableStateFlow(MutedColorScheme.default)
    val currentScheme: StateFlow<MutedColorScheme> = _currentScheme.asStateFlow()

    // 所有可用主题
    val allSchemes = MutedColorScheme.entries.toList()

    init {
        // 从 DataStore 加载保存的主题
        viewModelScope.launch {
            themeManager.currentScheme.collect { scheme ->
                _currentScheme.update { scheme }
            }
        }
    }

    /**
     * 切换主题
     */
    fun setScheme(scheme: MutedColorScheme) {
        viewModelScope.launch {
            themeManager.setScheme(scheme)
            _currentScheme.update { scheme }
        }
    }
}