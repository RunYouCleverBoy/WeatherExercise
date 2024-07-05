package com.demoapps.weather.screens.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demoapps.weather.models.LocationModel
import com.demoapps.weather.models.WeatherModel
import com.demoapps.weather.repositories.WeatherRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(private val weatherRepo: WeatherRepo) : ViewModel() {
    private val _state = MutableStateFlow(WeatherScreenState())
    val state = _state.asStateFlow()

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
        }
    }

    private fun onLoadWeather(location: LocationModel) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, location = location) }
            val error = weatherRepo.fetchWeatherFromServer(location)
            if (error != null) {
                _state.update { it.copy(isLoading = false, error = error.readableMessage) }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
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