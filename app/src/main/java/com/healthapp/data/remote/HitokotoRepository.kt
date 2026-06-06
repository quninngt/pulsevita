package com.healthapp.data.remote

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for random quotes (Hitokoto API).
 * Replaces the old static HitokotoApi object — now injectable and testable.
 */
@Singleton
class HitokotoRepository @Inject constructor(
    private val hitokotoService: HitokotoService
) {
    data class Quote(val text: String, val from: String)

    suspend fun fetchQuote(): NetworkResult<Quote> = safeApiCall {
        val resp = hitokotoService.getRandomQuote()
        Quote(resp.text, resp.from)
    }
}
