package com.demoapps.weather.di

import com.demoapps.weather.repositories.GeolocationRepo
import com.demoapps.weather.repositories.impl.GeolocationRepoImpl
import com.demoapps.weather.repositories.Secrets
import com.demoapps.weather.repositories.impl.SecretsImpl
import com.demoapps.weather.repositories.WeatherRepo
import com.demoapps.weather.repositories.impl.WeatherRepoImpl
import com.demoapps.weather.repositories.api.impl.KtorNetworkingEngine
import com.demoapps.weather.repositories.api.NetworkingEngine
import com.demoapps.weather.repositories.api.WeatherApi
import com.demoapps.weather.repositories.api.impl.OpenWeatherApiImpl
import com.demoapps.weather.screens.placesearch.SearchViewModel
import com.demoapps.weather.screens.weather.WeatherViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

object AppModule {
    fun provideAppModule(): Module = module {
        // Note: Don't use bind notation, use single<Interface> { implementation }
        // to prevent direct access to the implementation
        single<GeolocationRepo> { GeolocationRepoImpl(get()) }
        single<WeatherRepo> { WeatherRepoImpl(get()) }
        single<NetworkingEngine> { KtorNetworkingEngine() }
        single<WeatherApi> {
            val secrets = get<Secrets>()
            OpenWeatherApiImpl(get(), secrets[Secrets.Items.OPEN_WEATHER_API_KEY])
        }
        single<Secrets> { SecretsImpl(get()) }
        viewModelOf(::SearchViewModel)
        viewModelOf(::WeatherViewModel)
    }
}