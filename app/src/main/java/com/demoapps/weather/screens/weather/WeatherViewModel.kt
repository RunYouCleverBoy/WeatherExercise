package com.demoapps.weather.screens.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demoapps.weather.R
import com.demoapps.weather.models.LocationModel
import com.demoapps.weather.models.WeatherModel
import com.demoapps.weather.repositories.WeatherRepo
import com.demoapps.weather.repositories.api.CallResult
import com.demoapps.weather.screens.MainDestinations
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class WeatherViewModel(private val weatherRepo: WeatherRepo) : ViewModel() {
    private val _state = MutableStateFlow(WeatherScreenState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<WeatherScreenEffect>()
    val effect = _effect.asSharedFlow()

    init {
        viewModelScope.launch {
            weatherRepo.weatherState.filterNotNull().collect { weatherData ->
                refreshDisplayData(weatherData)
            }
        }
    }

    fun dispatchEvent(event: WeatherScreenEvent) {
        when (event) {
            is WeatherScreenEvent.OnScreenLoad -> onLoadWeather(event.location)
            WeatherScreenEvent.OnTemperatureClicked -> onTemperatureClicked()
            WeatherScreenEvent.OnEditLocationClicked -> emit(WeatherScreenEffect.NavigateTo(MainDestinations.PlaceSearch))
            WeatherScreenEvent.OnReloadClicked -> state.value.location?.let { onLoadWeather(it) }
        }
    }

    private fun onLoadWeather(location: LocationModel) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, location = location) }
            val result = weatherRepo.fetchWeatherFromServer(location)
            val errorWording = when (result) {
                is CallResult.Failure.NotFound -> R.string.err_location_not_found
                is CallResult.Failure.ServerError -> R.string.err_server_error
                is CallResult.Failure.UnknownError -> R.string.err_unknown_error
                is CallResult.Failure.BadRequest -> R.string.err_bad_request
                else -> null
            }

            (result as? CallResult.Failure)?.let {
                Timber.w(it.exception, "Failed to fetch weather data. Showing $result")
            }

            val backgroundArt = when (errorWording) {
                null -> R.drawable.weather_background
                else -> R.drawable.faulty_weather_background
            }

            _state.update {
                it.copy(isLoading = false, error = errorWording, backgroundArt = backgroundArt)
            }
        }
    }

    private fun emit(effect: WeatherScreenEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    private fun onTemperatureClicked() {
        _state.update {
            it.copy(
                temperatureUnits = when (it.temperatureUnits) {
                    WeatherFormatters.TemperatureUnits.Celsius -> WeatherFormatters.TemperatureUnits.Fahrenheit
                    WeatherFormatters.TemperatureUnits.Fahrenheit -> WeatherFormatters.TemperatureUnits.Celsius
                }
            )
        }

        weatherRepo.weatherState.value?.let {
            refreshDisplayData(it)
        }
    }

    private fun refreshDisplayData(weatherData: WeatherModel) {
        val formatters = WeatherFormatters(state.value.temperatureUnits)
        _state.update {
            it.copy(
                weatherData = weatherData.toWeatherScreenState(weatherData.location, formatters)
            )
        }
    }
}