package app.weatherhistory.android.viewdetail


import app.weatherhistory.android.RetrofitUtil
import io.reactivex.Flowable
import java.io.File

class MonthlyAvgRepositoryRetrofit(val cache: File) : MonthlyAvgRepository {

    private val monthlyAvgService = RetrofitUtil.getInstance(cache)?.create(MonthlyAvgRepository::class.java)

    override fun getDataForStation(stationCode: String) = monthlyAvgService?.getDataForStation(stationCode)
            ?: Flowable.empty()
}