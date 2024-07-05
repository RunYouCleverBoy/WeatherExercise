package com.demoapps.weather.screens.weather

import androidx.annotation.DrawableRes
import com.demoapps.weather.models.LocationModel
import com.demoapps.weather.models.WeatherModel
import com.demoapps.weather.screens.MainDestinations

data class WeatherScreenState(
    val isLoading: Boolean = false,
    val weatherData: WeatherDisplayModel? = null,
    @DrawableRes val backgroundArt: Int = 0,
    val location: LocationModel? = null,
    val error: Int? = null,
    val temperatureUnits: WeatherFormatters.TemperatureUnits = WeatherFormatters.TemperatureUnits.Celsius
) {
    data class WeatherDisplayModel(
        val location: String = "",
        val temperatureModel: TemperatureModel = TemperatureModel(),
        val weatherCondition: String = "",
        val weatherIcon: String = ""
    ) {
        data class TemperatureModel(
            val temperature: String = "",
            val feelsLike: String = ""
        )
    }
}

sealed class WeatherScreenEvent {
    data class OnScreenLoad(val location: LocationModel) : WeatherScreenEvent()
    data object OnTemperatureClicked : WeatherScreenEvent()
    data object OnEditLocationClicked : WeatherScreenEvent()
    data object OnReloadClicked : WeatherScreenEvent()
}

sealed class WeatherScreenEffect {
    data class NavigateTo(val destination: MainDestinations) : WeatherScreenEffect()
}

fun WeatherModel.toWeatherScreenState(location: LocationModel, formatters: WeatherFormatters): WeatherScreenState.WeatherDisplayModel {
    val temperatureStr = temperature?.let { formatters.formatTemperature(it) }
    val feelsLikeStr = feelsLike?.let { formatters.formatTemperature(it) }
    return WeatherScreenState.WeatherDisplayModel(
        location = location.placeName,
        temperatureModel = WeatherScreenState.WeatherDisplayModel.TemperatureModel(
            temperature = temperatureStr ?: "",
            feelsLike = feelsLikeStr ?: ""
        ),
        weatherCondition = weatherDescription,
        weatherIcon = iconUri
    )
}




