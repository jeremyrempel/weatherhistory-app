package app.weatherhistory.android.viewdetail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import app.weatherhistory.android.R
import kotlinx.android.synthetic.main.fragment_viewdetail.*


class ViewDetailFragment : Fragment() {

    private lateinit var stationCode: String
    private lateinit var locationName: String

    companion object {
        fun getInstance(stationCode: String, locationName: String): Fragment {
            val b = Bundle()
            val frag = ViewDetailFragment()

            b.putString("stationcode", stationCode)
            b.putString("locationname", locationName)
            frag.arguments = b

            return frag
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stationCode = arguments?.getString("stationcode") ?: throw IllegalArgumentException("stationcode is required")
        locationName = arguments?.getString("locationname") ?: throw IllegalArgumentException("locationname is required")

        val model = ViewModelProviders.of(this).get(ViewDetailViewModel::class.java)
        setHasOptionsMenu(true)

        model.stationCode = stationCode
        model.getData().observe(this, Observer { it?.let { showData(it) } })

        showLoading()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_viewdetail, container, false)
    }

    private fun showLoading() {
        childFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, LoadingFragment.getInstance())
                .commitAllowingStateLoss()
    }

    private fun showData(data: List<MonthlyAverage>) {
        childFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, MonthlyChartFragment.getInstance())
                .commitAllowingStateLoss()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> activity?.supportFragmentManager?.popBackStack()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).apply {
            setSupportActionBar(my_toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = getString(R.string.monthly_average)
            supportActionBar?.subtitle = locationName
        }
    }
}