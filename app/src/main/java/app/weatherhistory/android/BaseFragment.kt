package app.weatherhistory.android

import android.content.Context
import android.support.v4.app.Fragment
import com.arlib.floatingsearchview.FloatingSearchView

open class BaseFragment : Fragment() {

    private var mCallbacks: BaseExampleFragmentCallbacks? = null

    interface BaseExampleFragmentCallbacks {
        fun onAttachSearchViewToDrawer(searchView: FloatingSearchView)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is BaseExampleFragmentCallbacks) {
            mCallbacks = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement BaseExampleFragmentCallbacks")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mCallbacks = null
    }

    protected fun attachSearchViewActivityDrawer(searchView: FloatingSearchView) {
        mCallbacks?.let { it.onAttachSearchViewToDrawer(searchView) }
    }
}