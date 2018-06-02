package app.weatherhistory.android

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout

import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import app.weatherhistory.android.search.LocationSearchFragment
import app.weatherhistory.android.viewdetail.ViewDetailFragment
import app.weatherhistory.android.viewdetail.ViewDetailViewModel
import com.arlib.floatingsearchview.FloatingSearchView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, LocationSearchFragment.BaseExampleFragmentCallbacks {

    private lateinit var drawerLayout: DrawerLayout
    private var currentScreen: CurrentScreen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val model = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        if (currentScreen == null) {
            currentScreen = CurrentScreen.Search("")
        }

        val frag = when (currentScreen) {
            is CurrentScreen.ViewOne -> {
                val s = currentScreen as CurrentScreen.ViewOne
                ViewDetailFragment.getInstance(s.stationCode, s.locationName)
            }
            else -> LocationSearchFragment()
        }

        showFragment(frag)

        val navigationView = nav_view
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNavigateToDetail(stationCode: String, locationName: String) {
        showFragment(ViewDetailFragment.getInstance(stationCode, locationName))
    }

    override fun onAttachSearchViewToDrawer(searchView: FloatingSearchView) = searchView.attachNavigationDrawerToMenuButton(drawer_layout)

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    sealed class CurrentScreen {
        data class Search(val a: String) : CurrentScreen()
        data class ViewOne(val stationCode: String, val locationName: String) : CurrentScreen()
    }
}
