package com.demoapps.weather.repositories

import android.content.Context
import android.content.pm.PackageManager

class SecretsImpl(context: Context) : Secrets {
    private val manifestMeta = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA).metaData

    override operator fun get(secret: Secrets.Items): String {
        val name = when (secret) {
            Secrets.Items.OPEN_WEATHER_API_KEY -> "OPEN_WEATHER_API_KEY"
        }
        return manifestMeta.getString(name) ?: throw IllegalArgumentException("Secret not found")
    }
}