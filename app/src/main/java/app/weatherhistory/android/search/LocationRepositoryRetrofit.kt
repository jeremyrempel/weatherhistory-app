package app.weatherhistory.android.search

import android.content.Context
import app.weatherhistory.model.Location
import io.reactivex.Flowable
import app.weatherhistory.android.RetrofitUtil

class LocationRepositoryRetrofit(val app: Context) : LocationRepository {

    private val locationService = RetrofitUtil.getInstance(app.cacheDir)?.create(LocationService::class.java)

    override fun findByName(name: String) = locationService?.findByName(name)
            ?: Flowable.empty()

    override fun getAll(): Flowable<List<Location>> = Flowable.empty()
}