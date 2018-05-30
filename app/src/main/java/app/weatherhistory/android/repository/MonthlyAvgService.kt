package app.weatherhistory.android.repository

import app.weatherhistory.model.Location
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface MonthlyAvgService {
    @GET("monthlyavg")
    fun get(@Query("stationcode") stationCode: String): Flowable<List<Location>>
}