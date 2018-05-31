package app.weatherhistory.android.viewdetail

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.weatherhistory.android.R
import kotlinx.android.synthetic.main.fragment_viewdetail.*
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem


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

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_viewdetail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.onViewCreated(view, savedInstanceState)
        station_code.text = stationCode
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> activity?.supportFragmentManager?.popBackStack()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val a = activity as AppCompatActivity
        a.setSupportActionBar(my_toolbar)
        a.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        a.supportActionBar?.title = getString(R.string.monthly_average)
        a.supportActionBar?.subtitle = locationName
    }
}