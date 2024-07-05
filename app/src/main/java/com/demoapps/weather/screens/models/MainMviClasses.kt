package com.demoapps.weather.screens.models

import com.demoapps.weather.models.LocationModel

sealed class MainEvent {
    data class OnPlaceSelected(val place: LocationModel) : MainEvent()
}

data class MainState(
    val selectedPlace: LocationModel? = null
)