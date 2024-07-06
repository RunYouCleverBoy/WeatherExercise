package com.demoapps.weather.repositories.api.impl

import android.net.Uri
import com.demoapps.weather.models.LocationModel
import com.demoapps.weather.models.WeatherModel
import com.demoapps.weather.models.isCoordinateValid
import com.demoapps.weather.repositories.api.CallResult
import com.demoapps.weather.repositories.api.NetworkingEngine
import com.demoapps.weather.repositories.api.WeatherApi
import com.demoapps.weather.repositories.api.responses.WeatherApiResponse

class OpenWeatherApiImpl(private val networkingEngine: NetworkingEngine, private val apiKey: String) : WeatherApi {
    // Weather API
    override suspend fun getWeather(locationModel: LocationModel): CallResult {
        // Get weather. Note that OneCall requires paid subscription (at least with credit card)
        val builder = Uri.Builder()
            .scheme("https")
            .authority("api.openweathermap.org")
            .appendPath("data")
            .appendPath("2.5")
            .appendPath("weather")
            .appendQueryParameter("appid", apiKey)
            .appendQueryParameter("units", "metric")

        if (locationModel.isCoordinateValid()) {
            builder.appendQueryParameter("lat", locationModel.latitude.toString())
            builder.appendQueryParameter("lon", locationModel.longitude.toString())
        } else {
            builder.appendQueryParameter("q", locationModel.placeName)
        }
        builder.appendQueryParameter("exclude", "minutely,daily,alerts,hourly")

        val requestUri = builder.build()
        return when (val response = networkingEngine.get(requestUri, WeatherApiResponse.serializer())) {
            is CallResult.Success<*> -> CallResult.Success((response.data as? WeatherApiResponse)?.toWeatherModel(locationModel))
            is CallResult.Failure -> response
        }
    }

    private fun getWeatherIconUrl(icon: String) = "https://openweathermap.org/img/wn/$icon.png"

    private fun WeatherApiResponse.toWeatherModel(locationModel: LocationModel): WeatherModel {
        return WeatherModel(
            location = locationModel,
            temperature = main.temp,
            feelsLike = main.feelsLike,
            weatherDescription = weather.firstOrNull()?.description ?: "",
            iconUri = weather.firstOrNull()?.icon?.let { getWeatherIconUrl(it) } ?: "",
        )
    }
}