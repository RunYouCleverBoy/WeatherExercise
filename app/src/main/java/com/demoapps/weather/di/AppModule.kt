package com.demoapps.weather.di

import com.demoapps.weather.repositories.GeolocationRepo
import com.demoapps.weather.repositories.Secrets
import com.demoapps.weather.repositories.WeatherRepo
import com.demoapps.weather.repositories.api.KtorEngine
import com.demoapps.weather.repositories.api.WeatherApi
import com.demoapps.weather.screens.placesearch.SearchViewModel
import com.demoapps.weather.screens.weather.WeatherViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

object AppModule {
    fun provideAppModule(): Module = module {
        single { GeolocationRepo(get()) }
        single { WeatherRepo(get(), get()) }
        single { KtorEngine() }
        single {
            val secrets = get<Secrets>()
            WeatherApi(get(), secrets[Secrets.Items.OPEN_WEATHER_API_KEY])
        }
        single { Secrets(get()) }
        viewModelOf(::SearchViewModel)
        viewModelOf(::WeatherViewModel)
    }
}