package com.demoapps.weather.repositories

import com.demoapps.weather.models.LocationModel
import com.demoapps.weather.models.WeatherModel
import com.demoapps.weather.repositories.api.CallResult
import com.demoapps.weather.repositories.api.WeatherApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WeatherRepoImpl(private val weatherApi: WeatherApi) : WeatherRepo {
    // Note: Since the weather service costs (theoretically) money,
    // we shall try to avoid calling the server too often.

    private val _weatherState = MutableStateFlow<WeatherModel?>(null)
    override val weatherState = _weatherState.asStateFlow()

    override suspend fun fetchWeatherFromServer(locationModel: LocationModel): CallResult? {
        when (val response = weatherApi.getWeather(locationModel)) {
            is CallResult.Success<*> -> (response.data as? WeatherModel)?.let { _weatherState.value = it }
            is CallResult.Failure -> return response
        }

        return null
    }
}
