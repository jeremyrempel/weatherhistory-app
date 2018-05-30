package app.weatherhistory.android.repository


import io.reactivex.Flowable
import java.io.File

class MonthlyAvgRepositoryRetrofit(val cache: File) : MonthlyAvgRepository {

    private val monthlyAvgService = Retrofit.getInstance(cache)?.create(MonthlyAvgRepository::class.java)

    override fun getDataForStation(stationCode: String) = monthlyAvgService?.getDataForStation(stationCode)
            ?: Flowable.empty()
}