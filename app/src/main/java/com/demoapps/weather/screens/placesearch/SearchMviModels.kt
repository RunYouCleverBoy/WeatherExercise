package com.demoapps.weather.screens.placesearch

import com.demoapps.weather.models.LocationModel

sealed class PlaceSearchEffect {
    data class FinishWithLocation(val location: LocationModel): PlaceSearchEffect()
}

sealed class PlaceSearchEvent {
    data class OnTextChanged(val text: String): PlaceSearchEvent()
    data class OnPlaceSelected(val place: LocationModel): PlaceSearchEvent()
}

data class PlaceSearchState(
    val isLoading : Boolean = false,
    val searchQuery: String = "",
    val suggestions: List<LocationModel> = emptyList(),
    val showSuggestions: Boolean = false
)