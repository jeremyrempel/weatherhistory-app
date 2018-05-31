package app.weatherhistory.android.data

import android.os.Parcel
import android.os.Parcelable
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion

class LocationSuggestion(var stationCode: String, private var name: String, private var state: String?, private var countryCode: String) : SearchSuggestion {

    constructor(parcel: Parcel) : this(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString()) {
        parcel.apply {
            stationCode = readString()
            name = readString()
            state = readString()
            countryCode = readString()
        }
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeString(stationCode)
            writeString(name)
            writeString(state)
        }
    }

    override fun describeContents() = 0

    override fun getBody(): String = if (state.isNullOrBlank()) "$name, $countryCode" else "$name, $state"

    companion object CREATOR : Parcelable.Creator<LocationSuggestion> {
        override fun createFromParcel(parcel: Parcel): LocationSuggestion {
            return LocationSuggestion(parcel)
        }

        override fun newArray(size: Int): Array<LocationSuggestion?> {
            return arrayOfNulls(size)
        }
    }
}