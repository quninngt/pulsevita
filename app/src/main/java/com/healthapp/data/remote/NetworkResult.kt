package com.healthapp.data.remote

/**
 * Sealed wrapper for network call results.
 * Eliminates scattered try/catch and gives UI a clear signal.
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

/**
 * Safe network call wrapper. Catches exceptions and returns NetworkResult.
 */
suspend fun <T> safeApiCall(call: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(call())
    } catch (e: Exception) {
        NetworkResult.Error(e.localizedMessage ?: "网络请求失败", e)
    }
}
