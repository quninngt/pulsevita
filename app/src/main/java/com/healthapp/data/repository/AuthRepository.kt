package com.healthapp.data.repository

import com.healthapp.data.remote.LoginRequest
import com.healthapp.data.remote.RegisterRequest
import com.healthapp.data.remote.ServerApiService
import com.healthapp.data.remote.TokenManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    data class Success(val nickname: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

@Singleton
class AuthRepository @Inject constructor(
    private val serverApiService: ServerApiService,
    private val tokenManager: TokenManager
) {
    suspend fun register(username: String, password: String, nickname: String): AuthResult {
        return try {
            val response = serverApiService.register(
                RegisterRequest(username = username, password = password, nickname = nickname)
            )
            if (response.isSuccessful && response.body()?.code == 200) {
                val data = response.body()!!.data!!
                tokenManager.saveTokens(data.accessToken, data.refreshToken)
                AuthResult.Success(data.nickname)
            } else {
                val msg = response.body()?.message ?: "注册失败: ${response.code()}"
                AuthResult.Error(msg)
            }
        } catch (e: Exception) {
            AuthResult.Error("网络错误: ${e.localizedMessage}")
        }
    }

    suspend fun login(username: String, password: String): AuthResult {
        return try {
            val response = serverApiService.login(
                LoginRequest(username = username, password = password)
            )
            if (response.isSuccessful && response.body()?.code == 200) {
                val data = response.body()!!.data!!
                tokenManager.saveTokens(data.accessToken, data.refreshToken)
                AuthResult.Success(data.nickname)
            } else {
                val msg = response.body()?.message ?: "登录失败: ${response.code()}"
                AuthResult.Error(msg)
            }
        } catch (e: Exception) {
            AuthResult.Error("网络错误: ${e.localizedMessage}")
        }
    }

    suspend fun logout() {
        tokenManager.clearTokens()
    }

    suspend fun refreshToken(): Boolean {
        return try {
            val refreshToken = tokenManager.getRefreshTokenBlocking() ?: return false
            val response = serverApiService.refreshToken(mapOf("refreshToken" to refreshToken))
            if (response.isSuccessful && response.body()?.code == 200) {
                val data = response.body()!!.data!!
                tokenManager.saveTokens(data.accessToken, data.refreshToken)
                true
            } else {
                tokenManager.clearTokens()
                false
            }
        } catch (_: Exception) {
            tokenManager.clearTokens()
            false
        }
    }

    fun isLoggedIn(): Flow<Boolean> = tokenManager.isLoggedIn()
}
