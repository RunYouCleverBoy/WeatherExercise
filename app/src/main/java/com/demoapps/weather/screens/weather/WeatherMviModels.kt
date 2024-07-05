package com.demoapps.weather.screens.weather

import com.demoapps.weather.models.LocationModel
import com.demoapps.weather.models.WeatherModel
import com.demoapps.weather.utils.formatAccuracy

data class WeatherScreenState(
    val isLoading: Boolean = false,
    val weatherData: WeatherDisplayModel? = null,
    val location: LocationModel? = null,
    val error: String? = null,
    val temperatureUnits: WeatherFormatters.TemperatureUnits = WeatherFormatters.TemperatureUnits.Celsius
) {
    data class WeatherDisplayModel(
        val location: String = "",
        val temperature: String = "",
        val weatherCondition: String = "",
        val weatherIcon: String = ""
    )
}

sealed class WeatherScreenEvent {
    data class OnScreenLoad(val location: LocationModel) : WeatherScreenEvent()
    data object OnTemperatureClicked : WeatherScreenEvent()
}

fun WeatherModel.toWeatherScreenState(location: LocationModel, formatters: WeatherFormatters): WeatherScreenState.WeatherDisplayModel {
    val temperatureStr = temperature?.let { formatters.formatTemperature(it) }
    val feelsLikeStr = feelsLike?.let { formatters.formatTemperature(it) }
    return WeatherScreenState.WeatherDisplayModel(
        location = location.placeName,
        temperature = if (feelsLikeStr != null) {
            "$temperatureStr ($feelsLikeStr)"
        } else {
            temperatureStr ?: ""
        },
        weatherCondition = weatherDescription,
        weatherIcon = iconUri
    )
}



