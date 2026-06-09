package com.healthapp.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 服务器地址配置，支持用户自定义
 */
@Singleton
class ServerConfig @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("server_config", Context.MODE_PRIVATE)
    }

    /**
     * 获取当前服务器地址（优先使用用户自定义，否则使用默认值）
     */
    fun getServerUrl(): String {
        return prefs.getString(KEY_SERVER_URL, null) ?: Constants.SERVER_BASE_URL
    }

    /**
     * 设置服务器地址
     */
    fun setServerUrl(url: String) {
        val normalizedUrl = if (url.endsWith("/")) url else "$url/"
        prefs.edit().putString(KEY_SERVER_URL, normalizedUrl).apply()
    }

    /**
     * 重置为默认地址
     */
    fun resetToDefault() {
        prefs.edit().remove(KEY_SERVER_URL).apply()
    }

    /**
     * 是否使用自定义地址
     */
    fun isCustomUrl(): Boolean {
        return prefs.contains(KEY_SERVER_URL)
    }

    companion object {
        private const val KEY_SERVER_URL = "custom_server_url"
    }
}
