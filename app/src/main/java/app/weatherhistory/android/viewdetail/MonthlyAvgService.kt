package app.weatherhistory.android.viewdetail

import app.weatherhistory.android.search.Location
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface MonthlyAvgService {
    @GET("monthlyavg")
    fun get(@Query("stationcode") stationCode: String): Flowable<List<Location>>
}