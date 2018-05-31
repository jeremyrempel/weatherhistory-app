package app.weatherhistory.android.search

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import app.weatherhistory.android.R
import app.weatherhistory.model.Location
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_location_search.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

class LocationSearchFragment : Fragment() {

    private lateinit var dimDrawable: ColorDrawable

    private var lastQuery = ""
    private val ANIM_DURATION: Long = 350
    var callback: LocationSearchFragment.BaseExampleFragmentCallbacks? = null


    /**
     * The alpha value to set, between 0 and 255
     */
    val FADE_DIM_BG = 150

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_location_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dimSearchViewBackground = dim_background
        dimDrawable = ColorDrawable(Color.BLACK)
        dimDrawable.alpha = 0
        dimSearchViewBackground.background = dimDrawable

        setupFloatingSearch()
        setupDrawer()
    }

    private fun setupDrawer() = callback?.let { it.onAttachSearchViewToDrawer(floating_search_view) }

    private fun setupSearchSubject(searchView: FloatingSearchView): PublishSubject<String> {
        val subject = PublishSubject.create<String>()

        val appContext = context ?: throw IllegalArgumentException("No application context")

        val locationService = LocationRepositoryRetrofit(appContext)

        subject
                .filter { it.isNotBlank() }
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .switchMap {
                    locationService.findByName(it)
                            .retry(1)
                            .onExceptionResumeNext { emptyList<Location>() }
                            .toObservable()
                }
                .map { it.map { LocationSuggestion(it.stationCode, it.name, it.state, it.countryCode) } }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            Timber.d("onSearchComplete. Results: ${it.size}")
                            searchView.swapSuggestions(it)
                            searchView.hideProgress()

                        },
                        {
                            Timber.e(it)
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            searchView.hideProgress()
                        }
                )

        return subject
    }

    private fun setupFloatingSearch() {

        val searchView = floating_search_view
        val subject = setupSearchSubject(searchView)

        searchView.setOnQueryChangeListener({ oldQuery, newQuery ->
            if (oldQuery != "" && newQuery == "") {
                searchView.clearSuggestions()
            } else {
                searchView.showProgress()
                subject.onNext(newQuery)
            }

            Timber.d("onSearchTextChanged($newQuery)")
        })

        searchView.setOnSearchListener(
                object : FloatingSearchView.OnSearchListener {
                    override fun onSearchAction(currentQuery: String?) {
                        // do something?
                    }

                    override fun onSuggestionClicked(searchSuggestion: SearchSuggestion) {

                        if (searchSuggestion is LocationSuggestion) {
                            lastQuery = ""
                            searchView.clearFocus()
                            Timber.d("onSuggestionClicked(), stationCode: ${searchSuggestion.stationCode}")

                            callback?.onNavigateToDetail(searchSuggestion.stationCode, searchSuggestion.name)
                        }
                    }
                })

        searchView.setOnFocusChangeListener(
                object : FloatingSearchView.OnFocusChangeListener {
                    override fun onFocus() {
                        val headerHeight = resources.getDimensionPixelOffset(R.dimen.sliding_search_view_header_height)

                        val anim = ObjectAnimator.ofFloat(searchView, "translationY", headerHeight.toFloat(), 0f)
                        anim.duration = ANIM_DURATION
                        fadeDimBackground(0, FADE_DIM_BG, null)
                        anim.addListener(object : AnimatorListenerAdapter() {

                            override fun onAnimationEnd(animation: Animator) {
                                //show suggestions when search bar gains focus (typically history suggestions)
                                //searchView.swapSuggestions(DataHelper.getHistory(activity, 3))
                            }
                        })
                        anim.start()

                        Timber.d("onFocus()")
                    }

                    override fun onFocusCleared() {
                        val headerHeight = resources.getDimensionPixelOffset(R.dimen.sliding_search_view_header_height)
                        val anim = ObjectAnimator.ofFloat(searchView, "translationY", 0f, headerHeight.toFloat())
                        anim.duration = ANIM_DURATION
                        anim.start()
                        fadeDimBackground(FADE_DIM_BG, 0, null)

                        //set the title of the bar so that when focus is returned a new query begins
                        //               searchView.setSearchBarTitle(lastQuery)
                        //searchView.setSearchText(lastQuery)

                        // clear search results
                        searchView.setSearchBarTitle("")
                        searchView.setSearchText("")

                        //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                        //searchView.setSearchText(searchSuggestion.getBody());

                        Timber.d("onFocusCleared()")
                    }
                })


        //handle menu clicks the same way as you would
        //in a regular activity
        searchView.setOnMenuItemClickListener(
                { item ->

                    when (item.itemId) {
                        R.id.action_location -> {
                            // todo
                            /* query based on location */
                        }
                        else -> {
                            Timber.w("Unhandled action: $item")
                        }
                    }
                })

        /*
         * Here you have access to the left icon and the text of a given suggestion
         * item after as it is bound to the suggestion list. You can utilize this
         * callback to change some properties of the left icon and the text. For example, you
         * can load the left icon images using your favorite image loading library, or change text color.
         *
         *
         * Important:
         * Keep in mind that the suggestion list is a RecyclerView, so views are reused for different
         * items in the list.
         */
        searchView.setOnBindSuggestionCallback(
                { suggestionView, leftIcon, textView, item, itemPosition ->
                    val colorSuggestion = item

                    // todo show country flag
                    //leftIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_history_black_24dp, null))
                })

        /*
         * When the user types some text into the search field, a clear button (and 'x' to the
         * right) of the search text is shown.
         *
         * This listener provides a callback for when this button is clicked.
         */
        searchView.setOnClearSearchActionListener(
                { Timber.d("onClearSearchClicked()") })
    }

    private fun fadeDimBackground(from: Int, to: Int, listener: Animator.AnimatorListener?) {
        val anim = ValueAnimator.ofInt(from, to)
        anim.addUpdateListener { dimDrawable.alpha = it.animatedValue as Int }

        listener?.let { anim.addListener(listener) }
        anim.duration = ANIM_DURATION
        anim.start()
    }

    interface BaseExampleFragmentCallbacks {
        fun onAttachSearchViewToDrawer(searchView: FloatingSearchView)
        fun onNavigateToDetail(stationCode: String, locationName: String)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is LocationSearchFragment.BaseExampleFragmentCallbacks) {
            callback = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement BaseExampleFragmentCallbacks")
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }
}