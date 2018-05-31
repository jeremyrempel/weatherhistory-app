package app.weatherhistory.android.viewdetail

import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface MonthlyAvgRepository {
    @GET("monthlyavg")
    fun getDataForStation(@Query("stationcode") stationCode: String): Flowable<List<MonthlyAverage>>
}