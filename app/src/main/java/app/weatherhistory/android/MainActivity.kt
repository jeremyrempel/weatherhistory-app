package app.weatherhistory.android

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout

import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import app.weatherhistory.android.search.LocationSearchFragment
import app.weatherhistory.android.viewdetail.ViewDetailFragment
import com.arlib.floatingsearchview.FloatingSearchView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, LocationSearchFragment.FragmentCallbacks, ViewDetailFragment.FragmentCallbacks {

    private lateinit var model: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        model.getState().observe(this, Observer { it?.let { updateState(it) } })

        val navigationView = nav_view
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun updateState(newState: CurrentScreen) {
        when (newState) {
            is CurrentScreen.Search -> showFragment(LocationSearchFragment.getInstance(newState))
            is CurrentScreen.ViewOne -> showFragment(ViewDetailFragment.getInstance(newState), false)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNavigateHome() = model.gotoSearch()

    override fun onNavigateToDetail(stationCode: String, locationName: String) = model.gotoViewOne(stationCode, locationName)

    override fun onAttachSearchViewToDrawer(searchView: FloatingSearchView) = searchView.attachNavigationDrawerToMenuButton(drawer_layout)

    private fun showFragment(fragment: Fragment, addtoBackStack: Boolean = true) {
        val trans = supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
                .replace(R.id.fragment_container, fragment)

        if (addtoBackStack) trans.addToBackStack(null)
        trans.commit()
    }

    override fun onBackPressed() = onNavigateHome()
}
