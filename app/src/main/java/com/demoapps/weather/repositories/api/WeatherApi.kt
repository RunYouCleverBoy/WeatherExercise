package com.demoapps.weather.repositories.api

import android.net.Uri
import com.demoapps.weather.models.LocationModel
import com.demoapps.weather.models.RemoteQueryResult
import com.demoapps.weather.models.RequestError
import com.demoapps.weather.models.isCoordinateValid
import com.demoapps.weather.repositories.api.responses.WeatherApiResponse

class WeatherApi(private val ktorEngine: KtorEngine, private val apiKey: String) {
    // Weather API
    suspend fun getWeather(locationModel: LocationModel): RemoteQueryResult<WeatherApiResponse> {
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


        val response = try {
            ktorEngine.get(requestUri, WeatherApiResponse.serializer())
        } catch (e: Exception) {
            return RemoteQueryResult.Error(RequestError.Unknown(e.message ?: "Unknown error"))
        }
        return response.toRemoteQueryResult()
    }

    fun getWeatherIconUrl(icon: String) = "https://openweathermap.org/img/wn/$icon.png"

    private fun Result<WeatherApiResponse>.toRemoteQueryResult(): RemoteQueryResult<WeatherApiResponse> {
        val weather = getOrNull()
        return if (weather != null) {
            RemoteQueryResult.Success(weather)
        } else {
            RemoteQueryResult.Error(RequestError.Unknown(exceptionOrNull()?.message ?: "Unknown error"))
        }
    }
}