package com.demoapps.weather.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.demoapps.weather.models.LocationModel
import com.demoapps.weather.screens.placesearch.PlaceSearchScreen
import com.demoapps.weather.screens.weather.WeatherScreen

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var location by rememberSaveable {
        mutableStateOf<LocationModel?>(null)
    }
    Box(modifier = modifier) {
        NavHost(navController = navController, startDestination = MainDestinations.PlaceSearch.route) {
            composable(MainDestinations.PlaceSearch.route) {
                PlaceSearchScreen { locationModel ->
                    location = locationModel
                    navController.navigate(route = MainDestinations.Weather.route)
                }
            }
            composable(MainDestinations.Weather.route) {
                location?.let { locationModel ->
                    WeatherScreen(locationModel = locationModel) { destination ->
                        navController.navigate(route = destination.route) {
                            popUpTo(MainDestinations.PlaceSearch.route) {
                                inclusive = true
                            }
                        }
                    }
                } ?: run {
                    navController.navigate(route = MainDestinations.PlaceSearch.route)
                }
            }
        }
    }
}

