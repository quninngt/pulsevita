package com.healthapp.data.remote

import com.squareup.moshi.Json

// --- Hitokoto API ---

data class HitokotoResponse(
    @Json(name = "hitokoto") val text: String,
    @Json(name = "from") val from: String,
    @Json(name = "from_who") val fromWho: String?
)

// --- Weather API (Open-Meteo) ---

data class WeatherResponse(
    @Json(name = "current") val current: WeatherCurrent
)

data class WeatherCurrent(
    @Json(name = "temperature_2m") val temperature: Double,
    @Json(name = "weather_code") val weatherCode: Int
)

// --- IP Geolocation API ---

data class IpLocationResponse(
    @Json(name = "lat") val lat: Double,
    @Json(name = "lon") val lon: Double,
    @Json(name = "lng") val lng: Double = 0.0,
    @Json(name = "city") val city: String?,
    @Json(name = "status") val status: String?
) {
    /** Longitude field — ip-api uses "lon", ip.useragentinfo uses "lng" */
    val longitude: Double get() = if (lon != 0.0) lon else lng
}
