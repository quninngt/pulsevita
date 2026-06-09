package com.healthapp.data.remote

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import com.squareup.moshi.Moshi
import com.healthapp.util.ServerConfig
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val serverConfig: ServerConfig
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Don't add auth header to auth endpoints
        val path = originalRequest.url.encodedPath
        if (path.startsWith("/api/auth/")) {
            return chain.proceed(originalRequest)
        }

        val accessToken = tokenManager.getAccessTokenBlocking()
        val request = if (!accessToken.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(request)

        // If 401, try to refresh token
        if (response.code == 401) {
            response.close()
            val refreshed = refreshTokenSynchronized()
            if (refreshed) {
                val newToken = tokenManager.getAccessTokenBlocking()
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
                return chain.proceed(newRequest)
            } else {
                runBlocking { tokenManager.clearTokens() }
                return chain.proceed(originalRequest)
            }
        }

        return response
    }

    private fun refreshTokenSynchronized(): Boolean {
        val refreshToken = tokenManager.getRefreshTokenBlocking() ?: return false

        return try {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            val jsonAdapter = moshi.adapter(RefreshRequest::class.java)
            val body = jsonAdapter.toJson(RefreshRequest(refreshToken))
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(serverConfig.getServerUrl() + "api/auth/refresh")
                .post(body)
                .build()

            val client = OkHttpClient()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: return false
                val respAdapter = moshi.adapter(RefreshResponse::class.java)
                val resp = respAdapter.fromJson(responseBody)
                if (resp?.code == 200 && resp.data != null) {
                    runBlocking {
                        tokenManager.saveTokens(resp.data.accessToken, resp.data.refreshToken)
                    }
                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    private data class RefreshRequest(val refreshToken: String)
    private data class RefreshResponse(val code: Int, val message: String, val data: RefreshData?)
    private data class RefreshData(val accessToken: String, val refreshToken: String)
}
