package com.demoapps.weather.repositories.api

import android.net.Uri
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json

class KtorEngine {
    private val client: HttpClient = createKtorClient()
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun <T> get(uri: Uri, deserializerStrategy: DeserializationStrategy<T>): Result<T> {
        val response = client.get(uri.toString())
        return if (response.status.isSuccess()) {
            val text = response.bodyAsText()
            Result.success(json.decodeFromString(deserializerStrategy, text))
        } else {
            Result.failure(Exception("Failed to fetch data ${response.status}"))
        }
    }

    private fun createKtorClient(): HttpClient {
        val ktor = HttpClient(Android) {
            install(ContentNegotiation)

            // Configure engine timeouts
            engine {
                connectTimeout = 100_000
                socketTimeout = 100_000
            }
        }
        return ktor
    }
}