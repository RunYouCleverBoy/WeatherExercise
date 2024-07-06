package com.demoapps.weather.repositories

import com.demoapps.weather.models.LocationModel

interface GeolocationRepo {
    suspend fun suggestPlaceByName(name: String, maxResults: Int): List<LocationModel>
}