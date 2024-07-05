package com.demoapps.weather.repositories

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import com.demoapps.weather.models.LocationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration.Companion.seconds

class GeolocationRepo(context: Context) {
    private val geocoder = Geocoder(context)

    suspend fun suggestPlaceByName(name: String, maxResults: Int): List<LocationModel> {
        // Suggest place by name
        val addresses = try {
            withTimeout(3.seconds) {
                geolocationSearch(name, maxResults)
            }
        } catch (e: Exception) {
            Timber.w("Failed to get geolocation for $name due to ${e.message}")
            null
        }

        return addresses?.map { address ->
            LocationModel(
                latitude = address.latitude,
                longitude = address.longitude,
                placeName = (0.. address.maxAddressLineIndex).joinToString(", ") { address.getAddressLine(it) }
            )
        } ?: emptyList()
    }

    private suspend fun geolocationSearch(name: String, maxResults: Int): MutableList<Address>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        suspendCancellableCoroutine { continuation ->
            geocoder.getFromLocationName(name, maxResults) { addresses ->
                continuation.resume(addresses)
            }
        }
    } else {
        @Suppress("DEPRECATION")
        withContext(Dispatchers.IO) {
            geocoder.getFromLocationName(name, 5)
        }
    }
}