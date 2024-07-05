package com.demoapps.weather.repositories

import com.demoapps.weather.models.LocationModel
import com.demoapps.weather.models.WeatherModel
import com.demoapps.weather.repositories.api.CallResult
import com.demoapps.weather.repositories.api.WeatherApi
import com.demoapps.weather.repositories.api.responses.WeatherApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WeatherRepo(private val weatherApi: WeatherApi) {
    // Note: Since the weather service costs (theoretically) money,
    // we shall try to avoid calling the server too often.

    private val _weatherState = MutableStateFlow<WeatherModel?>(null)
    val weatherState = _weatherState.asStateFlow()

    suspend fun fetchWeatherFromServer(locationModel: LocationModel): CallResult? {
        when (val response = weatherApi.getWeather(locationModel)) {
            is CallResult.Success<*> -> _weatherState.value = (response.data as? WeatherApiResponse)?.toWeatherModel(locationModel)
            is CallResult.Failure -> return response
        }

        return null
    }

    private fun WeatherApiResponse.toWeatherModel(locationModel: LocationModel): WeatherModel {
        return WeatherModel(
            location = locationModel,
            temperature = main.temp,
            feelsLike = main.feelsLike,
            weatherDescription = weather.firstOrNull()?.description ?: "",
            iconUri = weather.firstOrNull()?.icon?.let { weatherApi.getWeatherIconUrl(it) } ?: "",
        )
    }
}
