package com.healthapp.data.remote

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Clean wrapper around IP geolocation + weather fetching.
 * Replaces the old static WeatherApi object — now injectable and testable.
 */
@Singleton
class WeatherRepository @Inject constructor(
    private val weatherService: WeatherService,
    private val ipApiService: IpApiService,
    private val ipFallbackService: IpFallbackService
) {
    data class WeatherInfo(
        val temperature: String,
        val weatherCode: Int,
        val city: String
    )

    data class LocationInfo(
        val lat: Double,
        val lon: Double,
        val city: String
    )

    /**
     * Fetch current weather. Tries primary IP service, falls back to secondary.
     */
    suspend fun fetchWeather(): NetworkResult<WeatherInfo> = safeApiCall {
        val location = fetchLocation()
        val response = weatherService.getCurrentWeather(location.lat, location.lon)
        WeatherInfo(
            temperature = "${response.current.temperature.toInt()}°C",
            weatherCode = response.current.weatherCode,
            city = location.city
        )
    }

    /**
     * Fetch IP-based location with fallback chain.
     */
    private suspend fun fetchLocation(): LocationInfo {
        // Try primary
        try {
            val resp = ipApiService.getLocation()
            if (resp.status == "success") {
                return LocationInfo(resp.lat, resp.longitude, resp.city ?: "")
            }
        } catch (_: Exception) { }

        // Try fallback
        try {
            val resp = ipFallbackService.getLocation()
            return LocationInfo(
                resp.lat,
                resp.longitude,
                resp.city ?: "北京"
            )
        } catch (_: Exception) { }

        // Ultimate fallback: Beijing
        return LocationInfo(39.9, 116.4, "北京")
    }

    companion object {
        fun getWeatherEmoji(code: Int): String = when (code) {
            in 0..1 -> "☀️"
            2 -> "⛅"
            3 -> "☁️"
            45, 48 -> "🌫️"
            51, 53, 55 -> "🌦️"
            61, 63, 65 -> "🌧️"
            71, 73, 75 -> "🌨️"
            80, 81, 82 -> "🌦️"
            95, 96, 99 -> "⛈️"
            else -> "🌤️"
        }
    }
}
