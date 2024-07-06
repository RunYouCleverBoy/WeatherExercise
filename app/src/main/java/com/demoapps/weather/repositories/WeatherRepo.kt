package com.demoapps.weather.repositories

import com.demoapps.weather.models.LocationModel
import com.demoapps.weather.models.WeatherModel
import com.demoapps.weather.repositories.api.CallResult
import kotlinx.coroutines.flow.StateFlow

interface WeatherRepo {
    val weatherState: StateFlow<WeatherModel?>

    suspend fun fetchWeatherFromServer(locationModel: LocationModel): CallResult?
}