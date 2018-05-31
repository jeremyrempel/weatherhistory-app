package app.weatherhistory.android.data

import android.os.Parcel
import android.os.Parcelable
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion

class LocationSuggestion(private var stationCode: String) : SearchSuggestion {

    constructor(parcel: Parcel) : this(parcel.readString()) {
        stationCode = parcel.readString()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(stationCode)
    }

    override fun describeContents() = 0

    override fun getBody() = stationCode

    companion object CREATOR : Parcelable.Creator<LocationSuggestion> {
        override fun createFromParcel(parcel: Parcel): LocationSuggestion {
            return LocationSuggestion(parcel)
        }

        override fun newArray(size: Int): Array<LocationSuggestion?> {
            return arrayOfNulls(size)
        }
    }
}