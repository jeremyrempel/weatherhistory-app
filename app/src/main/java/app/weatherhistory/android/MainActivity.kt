package app.weatherhistory.android

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

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, LocationSearchFragment.BaseExampleFragmentCallbacks {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showFragment(LocationSearchFragment())

        drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
        val navigationView = nav_view
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNavigateToDetail(stationCode: String, locationName: String) {
        showFragment(ViewDetailFragment.getInstance(stationCode, locationName))
    }

    override fun onAttachSearchViewToDrawer(searchView: FloatingSearchView) = searchView.attachNavigationDrawerToMenuButton(drawerLayout)

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
                .replace(R.id.fragment_container, fragment)
                .commitAllowingStateLoss()
    }
}
