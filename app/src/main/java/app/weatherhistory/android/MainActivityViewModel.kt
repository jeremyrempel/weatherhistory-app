package app.weatherhistory.android

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    private val state: MutableLiveData<CurrentScreen> by lazy {
        val s = MutableLiveData<CurrentScreen>()
        s.value = getDefaultState()
        s
    }

    private fun getDefaultState() = CurrentScreen.Search
    fun getState(): LiveData<CurrentScreen> = state

    fun gotoViewOne(stationCode: String, locationName: String) {
        state.value = CurrentScreen.ViewOne(stationCode, locationName)
    }
}

sealed class CurrentScreen {
    data class ViewOne(val stationCode: String, val locationName: String) : CurrentScreen()
    object Search : CurrentScreen()
}