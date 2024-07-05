package com.demoapps.weather.repositories.api

sealed class CallResult {
    data class Success<T>(val data: T) : CallResult()
    sealed class Failure(val exception: Exception) : CallResult() {
        data class BadRequest(val error: Exception) : Failure(error)
        data class NotFound(val error: Exception) : Failure(error)
        data class ServerError(val error: Exception) : Failure(error)
        data class UnknownError(val error: Exception) : Failure(error)
    }
}