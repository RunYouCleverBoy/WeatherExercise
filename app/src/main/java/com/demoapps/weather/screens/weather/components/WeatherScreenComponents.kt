package com.demoapps.weather.screens.weather.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.demoapps.weather.R
import com.demoapps.weather.screens.weather.WeatherScreenEvent
import com.demoapps.weather.screens.weather.WeatherScreenState

@Composable
fun LocationRow(location: String) {
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
fun WeatherIconAndTempRow(weatherIcon: String, temperature: WeatherScreenState.WeatherDisplayModel.TemperatureModel, onClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClicked),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AsyncImage(modifier = Modifier.aspectRatio(1.0f), model = weatherIcon, contentDescription = stringResource(id = R.string.weather_icon_content_desc))
        LabeledText(label = stringResource(id = R.string.temperature), text = temperature.temperature)
        LabeledText(label = stringResource(id = R.string.feels_like), text = temperature.feelsLike)
    }
}

@Composable
fun WeatherConditionRow(modifier: Modifier, weatherCondition: String) {
    Text(modifier = modifier, text = weatherCondition, style = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center))
}

@Composable
fun Controls(modifier: Modifier = Modifier, isEnabled: Boolean, onEvent: (WeatherScreenEvent) -> Unit) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(enabled = isEnabled, onClick = { onEvent(WeatherScreenEvent.OnReloadClicked) }) {
            Icon(painter = painterResource(id = R.drawable.ic_reload), contentDescription = stringResource(id = R.string.refresh_weather_content_desc))
        }
        IconButton(enabled = isEnabled, onClick = { onEvent(WeatherScreenEvent.OnEditLocationClicked) }) {
            Icon(painter = painterResource(id = R.drawable.ic_edit_location), contentDescription = stringResource(id = R.string.change_location_content_desc))
        }
    }
}

@Composable
fun ErrorRow(error: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White.copy(alpha = 0.5f)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = error, style = MaterialTheme.typography.headlineLarge.copy(color = Color.Red, textAlign = TextAlign.Center))
    }
}