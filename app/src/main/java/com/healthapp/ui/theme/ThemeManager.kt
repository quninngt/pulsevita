package com.healthapp.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore 扩展
val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

/**
 * 主题管理器
 * 负责主题切换和持久化存储
 */
class ThemeManager(private val context: Context) {

    companion object {
        private val COLOR_SCHEME_KEY = stringPreferencesKey("color_scheme")
        private const val DEFAULT_SCHEME = "MORANDI_BLUE_GREEN"
    }

    /**
     * 获取当前配色方案的 Flow
     */
    val currentScheme: Flow<MutedColorScheme> = context.themeDataStore.data
        .map { preferences ->
            val schemeName = preferences[COLOR_SCHEME_KEY] ?: DEFAULT_SCHEME
            MutedColorScheme.fromName(schemeName)
        }

    /**
     * 切换配色方案
     */
    suspend fun setScheme(scheme: MutedColorScheme) {
        context.themeDataStore.edit { preferences ->
            preferences[COLOR_SCHEME_KEY] = scheme.name
        }
    }

    /**
     * 获取所有可用的配色方案
     */
    fun getAllSchemes(): List<MutedColorScheme> {
        return MutedColorScheme.entries.toList()
    }
}