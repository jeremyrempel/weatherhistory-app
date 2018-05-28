package app.weatherhistory.weatherhistory

import android.app.Application
import timber.log.Timber

class WeatherHistoryApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // timber setup
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}