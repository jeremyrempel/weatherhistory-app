package app.weatherhistory.android.viewdetail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import app.weatherhistory.android.CurrentScreen
import app.weatherhistory.android.R
import kotlinx.android.synthetic.main.fragment_viewdetail.*


class ViewDetailFragment : Fragment() {

    private lateinit var stationCode: String
    private lateinit var locationName: String
    var callback: FragmentCallbacks? = null

    companion object {
        fun getInstance(state: CurrentScreen.ViewOne): Fragment {
            val b = Bundle()
            val frag = ViewDetailFragment()

            b.putString("stationcode", state.stationCode)
            b.putString("locationname", state.locationName)
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
        model.getData().observe(this, Observer { it?.let { showData() } })

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

    private fun showData() {
        childFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, MonthlyChartFragment.getInstance())
                .commitAllowingStateLoss()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> callback?.onNavigateHome()
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

    interface FragmentCallbacks {
        fun onNavigateHome()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is FragmentCallbacks) {
            callback = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement FragmentCallbacks")
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }
}