package app.weatherhistory.android.viewdetail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.weatherhistory.android.R
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_viewdetail_chart.*


class MonthlyChartFragment : Fragment() {

    companion object {
        fun getInstance(): Fragment {
            return MonthlyChartFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (parentFragment != null) {
            val model = ViewModelProviders.of(parentFragment as Fragment).get(ViewDetailViewModel::class.java)
            model.getData().observe(this, Observer { it?.let { showData(it) } })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_viewdetail_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chart = monthlytempchart
        chart.setNoDataText("Search for a location using action bar above")

        val months = arrayListOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        chart.xAxis.valueFormatter = IAxisValueFormatter { value, _ -> months[value.toInt() - 1] }
        chart.xAxis.labelCount = months.size
    }

    private fun showData(data: List<MonthlyAverage>) {
        val entries = data.map { BarEntry(it.month.toFloat(), it.maxtemp.celciusToFarenheit()) }
        val dataSet = BarDataSet(entries, getString(R.string.farenheit_label))
        dataSet.setColors(intArrayOf(R.color.secondaryColor), context)

        val chart = monthlytempchart
        chart.data = BarData(dataSet)
        chart.description.isEnabled = false
        chart.animateXY(3000, 3000)

        chart.invalidate()
    }
}

fun Float.celciusToFarenheit() = this * 9 / 5 + 32