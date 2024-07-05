package com.demoapps.weather.screens.placesearch

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.demoapps.weather.models.placeToLocationModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaceSearchScreen(onFinished: (LocationModel) -> Unit = {}) {
    val searchViewModel = koinViewModel<SearchViewModel>()
    val state by searchViewModel.state.collectAsState()
    val onSearchEvent = searchViewModel::dispatchEvent
    LaunchedEffect(key1 = Unit) {
        searchViewModel.effect.collect { effect ->
            when (effect) {
                is PlaceSearchEffect.FinishWithLocation -> onFinished(effect.location)
            }
        }
    }
    PlaceSearchUi(state, onSearchEvent)
}

@Composable
private fun PlaceSearchUi(
    state: PlaceSearchState,
    onSearchEvent: (PlaceSearchEvent) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.5f,
            painter = painterResource(id = R.drawable.select_location_image),
            contentDescription = stringResource(id = R.string.search_screen)
        )
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(40.dp),

                    value = state.searchQuery,
                    placeholder = { Text(text = stringResource(id = R.string.location_hint)) },
                    onValueChange = { text -> onSearchEvent(PlaceSearchEvent.OnTextChanged(text)) })

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(enabled = state.searchQuery.isNotBlank(), onClick = { onSearchEvent(PlaceSearchEvent.OnPlaceSelected(state.searchQuery.placeToLocationModel())) }) {
                    Icon(painter = painterResource(id = R.drawable.ic_send), contentDescription = stringResource(R.string.forecast_by_location_name))
                }
            }

            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (state.showSuggestions) {
                SuggestionsList(state.suggestions) {
                    onSearchEvent(PlaceSearchEvent.OnPlaceSelected(it))
                }
            }
        }
    }
}

@Composable
private fun SuggestionsList(suggestions: List<LocationModel>, onClicked: (LocationModel) -> Unit = {}) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(suggestions.size) { index ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable {
                    onClicked(suggestions[index])
                }) {
                Text(text = suggestions[index].placeName)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PlaceSearchEventPreview() {
    PlaceSearchUi(
        state = PlaceSearchState(
            isLoading = true,
            searchQuery = "testing",
            suggestions = listOf(
                LocationModel(0.0, 0.0, "Null Island"),
                LocationModel(40.0, 0.0, "Somewhere in Spain")
            ),
            showSuggestions = true
        )
    )
}