package com.demoapps.weather.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationModel(val latitude: Double, val longitude: Double, val placeName: String) : Parcelable

fun LocationModel.isCoordinateValid() = latitude in -90.0..90.0 && longitude in -180.0..180.0
fun String.placeToLocationModel() = LocationModel(-1000.0, -1000.0, this)
