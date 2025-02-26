package com.demoapps.weather.screens.weather

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demoapps.weather.R
import com.demoapps.weather.models.LocationModel
import com.demoapps.weather.screens.MainDestinations
import com.demoapps.weather.screens.weather.components.Controls
import com.demoapps.weather.screens.weather.components.ErrorRow
import com.demoapps.weather.screens.weather.components.LocationRow
import com.demoapps.weather.screens.weather.components.WeatherConditionRow
import com.demoapps.weather.screens.weather.components.WeatherIconAndTempRow
import org.koin.androidx.compose.koinViewModel

@Composable
fun WeatherScreen(locationModel: LocationModel, onNavigate: (MainDestinations) -> Unit = {}) {
    val weatherViewModel = koinViewModel<WeatherViewModel>()
    val state by weatherViewModel.state.collectAsState()
    LaunchedEffect(key1 = Unit) {
        weatherViewModel.dispatchEvent(WeatherScreenEvent.OnScreenLoad(locationModel))
        weatherViewModel.effect.collect {
            when (it) {
                is WeatherScreenEffect.NavigateTo -> onNavigate(it.destination)
            }
        }
    }

    WeatherScreenUi(state, weatherViewModel::dispatchEvent)
}

@Composable
private fun WeatherScreenUi(state: WeatherScreenState, onEvent: (WeatherScreenEvent) -> Unit = {}) {
    val weatherData = state.weatherData
    Box {
        if (state.backgroundArt > 0) {
            Image(
                modifier = Modifier.fillMaxSize(),
                alpha = 0.5f,
                contentScale = ContentScale.Crop,
                painter = painterResource(id = state.backgroundArt),
                contentDescription = stringResource(id = R.string.weather_screen_content_desc)
            )
        }
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        Column(modifier = Modifier.fillMaxSize()) {
            weatherData?.location?.let { LocationRow(it) }
            weatherData?.let {
                WeatherIconAndTempRow(weatherData.weatherIcon, weatherData.temperatureModel) {
                    onEvent(WeatherScreenEvent.OnTemperatureClicked)
                }
            }
            Row {
                weatherData?.weatherCondition?.let {
                    WeatherConditionRow(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        weatherData.weatherCondition
                    )
                } ?: Spacer(modifier = Modifier.weight(1f))
                Controls(isEnabled = !state.isLoading, onEvent = onEvent)
            }

            Spacer(modifier = Modifier.weight(1f))
            state.error?.let {
                ErrorRow(stringResource(id = it))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherScreenPreview() {
    WeatherScreenUi(
        state = WeatherScreenState(
            weatherData = WeatherScreenState.WeatherDisplayModel(
                location = "New York",
                temperatureModel = WeatherScreenState.WeatherDisplayModel.TemperatureModel(
                    temperature = "25°C",
                    feelsLike = "26°C"
                ),
                weatherCondition = "loreum ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt ut labore et dolore magna aliqua",
                weatherIcon = "https://openweathermap.org/img/wn/10d@x.png"
            ),
            backgroundArt = R.drawable.weather_background,
            isLoading = true,
            error = R.string.err_unknown_error
        )
    )
}
