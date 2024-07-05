package com.demoapps.weather.screens.weather

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.demoapps.weather.R
import com.demoapps.weather.models.LocationModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WeatherScreen(locationModel: LocationModel) {
    val weatherViewModel = koinViewModel<WeatherViewModel>()
    val state by weatherViewModel.state.collectAsState()
    LaunchedEffect(key1 = Unit) {
        weatherViewModel.dispatchEvent(WeatherScreenEvent.OnScreenLoad(locationModel))
    }

    WeatherScreenUi(state)
}

@Composable
private fun WeatherScreenUi(state: WeatherScreenState) {
    val weatherData = state.weatherData
    Box {
        val painterResource by remember(weatherData) {
            derivedStateOf {
                when {
                    weatherData != null -> R.drawable.weather_background
                    else -> R.drawable.stormy_ukiyo_e
                }
            }
        }

        Image(
            modifier = Modifier.fillMaxSize(),
            alpha = 0.5f,
            contentScale = ContentScale.Crop,
            painter = painterResource(id = painterResource),
            contentDescription = stringResource(id = R.string.weather_screen)
        )
        Column(modifier = Modifier.fillMaxSize()) {
            weatherData?.location?.let { LocationRow(it) }
            weatherData?.let {
                WeatherIconAndTempRow(weatherData.weatherIcon, weatherData.temperature)
            }
            weatherData?.weatherCondition?.let {
                WeatherConditionRow(weatherData.weatherCondition)
            }
            Spacer(modifier = Modifier.weight(1f))
            state.error?.let {
                ErrorRow(it)
            }
        }
    }
}

@Composable
private fun LocationRow(location: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = location, style = MaterialTheme.typography.headlineLarge.copy(textAlign = TextAlign.Center))
    }
}

@Composable
private fun WeatherIconAndTempRow(weatherIcon: String, temperature: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AsyncImage(modifier = Modifier.aspectRatio(1.0f), model = weatherIcon, contentDescription = temperature)
        Text(text = temperature, style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
private fun WeatherConditionRow(weatherCondition: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = weatherCondition, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ErrorRow(error: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp).background(Color.White.copy(alpha = 0.5f)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = error, style = MaterialTheme.typography.headlineLarge.copy(color = Color.Red, textAlign = TextAlign.Center))
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherScreenPreview() {
    WeatherScreenUi(
        state = WeatherScreenState(
            weatherData = WeatherScreenState.WeatherDisplayModel(
                location = "New York",
                temperature = "25°C",
                weatherCondition = "Sunny",
                weatherIcon = "https://openweathermap.org/img/wn/10d@x.png"
            ),
            error = "Error loading weather data"
        )
    )
}
