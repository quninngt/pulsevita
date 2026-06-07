package com.healthapp.data.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_tokens")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.tokenDataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = accessToken
            prefs[REFRESH_TOKEN] = refreshToken
        }
    }

    fun getAccessToken(): Flow<String?> = context.tokenDataStore.data.map { prefs ->
        prefs[ACCESS_TOKEN]
    }

    fun getRefreshToken(): Flow<String?> = context.tokenDataStore.data.map { prefs ->
        prefs[REFRESH_TOKEN]
    }

    suspend fun clearTokens() {
        context.tokenDataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN)
            prefs.remove(REFRESH_TOKEN)
        }
    }

    fun isLoggedIn(): Flow<Boolean> = context.tokenDataStore.data.map { prefs ->
        !prefs[ACCESS_TOKEN].isNullOrBlank()
    }

    fun getAccessTokenBlocking(): String? = runBlocking {
        getAccessToken().first()
    }

    fun getRefreshTokenBlocking(): String? = runBlocking {
        getRefreshToken().first()
    }
}
