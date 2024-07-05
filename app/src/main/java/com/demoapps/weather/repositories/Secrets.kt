package com.demoapps.weather.repositories

import android.content.Context
import android.content.pm.PackageManager

class Secrets(context: Context) {
    private val manifestMeta = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA).metaData

    enum class Items {
        OPEN_WEATHER_API_KEY
    }

    operator fun get(secret: Items): String {
        val name = when (secret) {
            Items.OPEN_WEATHER_API_KEY -> "OPEN_WEATHER_API_KEY"
        }
        return manifestMeta.getString(name) ?: throw IllegalArgumentException("Secret not found")
    }
}