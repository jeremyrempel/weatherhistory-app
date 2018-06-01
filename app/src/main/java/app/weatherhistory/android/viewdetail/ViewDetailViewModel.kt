package app.weatherhistory.android.viewdetail

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.LiveData
import app.weatherhistory.android.RetrofitUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ViewDetailViewModel : ViewModel() {

    lateinit var stationCode: String
    private var data: MutableLiveData<List<MonthlyAverage>>? = null

    private fun loadData() {
        RetrofitUtil.getInstance(null)

        // does need to be disposed if stream ends
        val repo = MonthlyAvgRepositoryRetrofit()
        repo
                .getDataForStation(stationCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    data?.value = it
                }, {
                    // fire bad!
                })
    }

    fun getData(): LiveData<List<MonthlyAverage>> {

        if (data == null) {
            data = MutableLiveData()
            loadData()
        }

        return data as LiveData<List<MonthlyAverage>>
    }
}