package com.demoapps.weather.screens.weather

import com.demoapps.weather.utils.formatAccuracy

class WeatherFormatters(private val temperatureUnit: TemperatureUnits) {
    enum class TemperatureUnits {
        Celsius,
        Fahrenheit
    }

    private val Double.toFahrenheit: Double get() = this * 9 / 5 + 32
    fun formatTemperature(temperature: Double): String {
        val (data, unitSymbol) = when (temperatureUnit) {
            TemperatureUnits.Celsius -> temperature to "°C"
            TemperatureUnits.Fahrenheit -> temperature.toFahrenheit to "°F"
        }
        return "${data.formatAccuracy(1)}$unitSymbol"
    }
}