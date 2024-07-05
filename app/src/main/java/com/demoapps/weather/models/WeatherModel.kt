package com.demoapps.weather.models


data class WeatherModel(
    val location: LocationModel,
    val temperature: Double?,
    val feelsLike: Double?,
    val weatherDescription: String,
    val iconUri: String,
)
