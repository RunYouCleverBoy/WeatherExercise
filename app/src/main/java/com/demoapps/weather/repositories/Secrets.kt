package com.demoapps.weather.repositories

interface Secrets {
    enum class Items {
        OPEN_WEATHER_API_KEY
    }

    operator fun get(secret: Items): String
}