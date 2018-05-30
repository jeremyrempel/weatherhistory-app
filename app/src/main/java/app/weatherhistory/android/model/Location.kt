package app.weatherhistory.model

/**
 * Created by jrempel on 1/27/18.
 */
data class Location(
        val stationCode: String,
        val name: String,
        val countryCode: String?,
        val state: String?
)