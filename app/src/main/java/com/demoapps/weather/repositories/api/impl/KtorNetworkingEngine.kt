package com.demoapps.weather.repositories.api.impl

import android.net.Uri
import com.demoapps.weather.repositories.api.CallResult
import com.demoapps.weather.repositories.api.NetworkingEngine
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json

class KtorNetworkingEngine : NetworkingEngine {
    private val client: HttpClient = createKtorClient()
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override suspend fun <T> get(uri: Uri, deserializerStrategy: DeserializationStrategy<T>): CallResult {
        val response = try {
            client.get(uri.toString())
        } catch (e: Exception) {
            return CallResult.Failure.UnknownError(e)
        }

        return if (response.status.isSuccess()) {
            val text = response.bodyAsText()
            CallResult.Success(json.decodeFromString(deserializerStrategy, text))
        } else {
            when (response.status) {
                HttpStatusCode.BadRequest -> CallResult.Failure.BadRequest(Exception("Bad request"))
                HttpStatusCode.NotFound -> CallResult.Failure.NotFound(Exception("Not found"))
                HttpStatusCode.InternalServerError -> CallResult.Failure.ServerError(Exception("Internal server error"))
                else -> CallResult.Failure.UnknownError(Exception("Unknown error"))
            }
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