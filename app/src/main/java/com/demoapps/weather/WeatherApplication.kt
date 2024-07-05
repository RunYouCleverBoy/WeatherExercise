package com.demoapps.weather

import android.app.Application
import com.demoapps.weather.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class WeatherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(level = Level.DEBUG)
            androidContext(this@WeatherApplication)
            val appModule = AppModule.provideAppModule()
            modules(appModule)
        }

        Timber.plant(Timber.DebugTree())
    }
}
