package com.healthapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Hitokoto — random quote API (v1.hitokoto.cn)
 */
interface HitokotoService {
    @GET(".")
    suspend fun getRandomQuote(): HitokotoResponse
}

/**
 * Open-Meteo — free weather API (no key required)
 */
interface WeatherService {
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "temperature_2m,weather_code",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}

/**
 * IP geolocation — primary (ip-api.com, HTTPS)
 */
interface IpApiService {
    @GET("json/")
    suspend fun getLocation(): IpLocationResponse
}

/**
 * IP geolocation — fallback (ip.useragentinfo.com, China-friendly)
 */
interface IpFallbackService {
    @GET("json")
    suspend fun getLocation(): IpLocationResponse
}
