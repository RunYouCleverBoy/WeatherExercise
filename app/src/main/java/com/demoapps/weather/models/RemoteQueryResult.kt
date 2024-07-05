package com.demoapps.weather.models

sealed class RemoteQueryResult<T> {
    data class Success<T>(val data: T) : RemoteQueryResult<T>()
    data class Error<T>(val errorType: RequestError) : RemoteQueryResult<T>()
}

sealed class RequestError(val readableMessage: String) {
    data class Unknown(val specificMessage: String) : RequestError("Unknown error\n$specificMessage")
}