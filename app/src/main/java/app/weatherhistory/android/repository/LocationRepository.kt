package app.weatherhistory.android.repository

import app.weatherhistory.model.Location
import io.reactivex.Flowable

/**
 * Created by jrempel on 1/27/18.
 */
interface LocationRepository {
    fun getAll(): Flowable<List<Location>>
    fun findByName(name: String): Flowable<List<Location>>
}