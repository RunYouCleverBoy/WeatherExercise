package com.demoapps.weather.repositories.api.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherApiResponse(
    @SerialName("weather") val weather: List<Weather>,
    @SerialName("main") val main: Main,
) {
    @Serializable
    data class Weather(
        @SerialName("main") val main: String,
        @SerialName("description") val description: String,
        @SerialName("icon") val icon: String,
    )

    @Serializable
    data class Main(
        @SerialName("temp") val temp: Double,
        @SerialName("feels_like") val feelsLike: Double,
    )
}
