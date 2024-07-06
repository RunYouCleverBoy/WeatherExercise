package com.demoapps.weather.repositories.api

import android.net.Uri
import kotlinx.serialization.DeserializationStrategy

interface NetworkingEngine {
    suspend fun <T> get(uri: Uri, deserializerStrategy: DeserializationStrategy<T>): CallResult
}