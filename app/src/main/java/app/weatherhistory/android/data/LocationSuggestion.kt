package app.weatherhistory.android.data

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import kotlinx.android.parcel.Parcelize

@Parcelize
class LocationSuggestion(private val stationCode: String) : SearchSuggestion {
    override fun getBody() = stationCode
}