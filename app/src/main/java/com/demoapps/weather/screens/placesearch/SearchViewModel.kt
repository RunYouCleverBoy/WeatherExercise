package com.demoapps.weather.screens.placesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demoapps.weather.repositories.GeolocationRepo
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class SearchViewModel(private val geolocationRepo: GeolocationRepo) : ViewModel() {
    private val _effect = MutableSharedFlow<PlaceSearchEffect>()
    val effect = _effect.asSharedFlow()

    private val _state = MutableStateFlow(PlaceSearchState())
    val state = _state.asStateFlow()

    private var geolocationJob: Job? by Delegates.observable(null) { _, old, new ->
        if (old != new && old?.isActive == true) {
            old.cancel()
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun dispatchEvent(event: PlaceSearchEvent) {
        when (event) {
            is PlaceSearchEvent.OnPlaceSelected -> emit(PlaceSearchEffect.FinishWithLocation(event.place))
            is PlaceSearchEvent.OnTextChanged -> onTextChange(event)
        }
    }

    private fun onTextChange(event: PlaceSearchEvent.OnTextChanged) {
        val searchQuery = event.text
        _state.update { it.copy(searchQuery = searchQuery) }
        if (searchQuery.length >= 3) {
            // Old job, if any, is automatically cancelled by delegate
            geolocationJob = viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                val suggestions = geolocationRepo.suggestPlaceByName(searchQuery, 10)
                _state.update { it.copy(isLoading = false, suggestions = suggestions, showSuggestions = suggestions.isNotEmpty()) }
            }
        }
    }

    private fun emit(effect: PlaceSearchEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}
