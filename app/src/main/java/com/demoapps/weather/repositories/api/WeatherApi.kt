package com.demoapps.weather.repositories.api

import com.demoapps.weather.models.LocationModel

interface WeatherApi {
    // Weather API
    suspend fun getWeather(locationModel: LocationModel): CallResult
}