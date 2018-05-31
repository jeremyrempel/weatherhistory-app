package app.weatherhistory.android.viewdetail

/**
 * Created by jrempel on 10/13/17.
 */
data class MonthlyAverage(
        val month: Int,
        val maxtemp: Float,
        val mintemp: Float,
        val precip: Float
)