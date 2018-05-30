package app.weatherhistory.android.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import app.weatherhistory.android.R
import app.weatherhistory.android.data.DataHelper
import app.weatherhistory.android.data.LocationSuggestion
import app.weatherhistory.android.repository.LocationRepositoryRetrofit
import app.weatherhistory.model.Location
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_location_search.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

class LocationSearchFragment : BaseFragment() {

    private lateinit var dimDrawable: ColorDrawable

    private var lastQuery = ""
    private val ANIM_DURATION: Long = 350
    val FIND_SUGGESTION_SIMULATED_DELAY: Long = 250

    /**
     * The alpha value to set, between 0 and 255
     */
    val FADE_DIM_BG = 150

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_location_search, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dimSearchViewBackground = dim_background
        dimDrawable = ColorDrawable(Color.BLACK)
        dimDrawable.alpha = 0
        dimSearchViewBackground.background = dimDrawable

        setupFloatingSearch()
        setupDrawer()
    }

    private fun setupDrawer() = attachSearchViewActivityDrawer(floating_search_view)

    private fun setupFloatingSearch() {

        val subject = PublishSubject.create<String>()
        val locationService = LocationRepositoryRetrofit(context.applicationContext)
        val searchView = floating_search_view


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
                .map { it.map { LocationSuggestion(it.name) } }
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

        searchView.setOnQueryChangeListener({ oldQuery, newQuery ->
            if (oldQuery != "" && newQuery == "") {
                searchView.clearSuggestions()
            } else {

                //this shows the top left circular progress
                //you can call it where ever you want, but
                //it makes sense to do it when loading something in
                //the background.

                searchView.showProgress()
                subject.onNext(newQuery)

//                //simulates a query call to a data source
//                //with a new query.
//                DataHelper.findSuggestions(activity, newQuery, 5, FIND_SUGGESTION_SIMULATED_DELAY) { results ->
//                    //this will swap the data and
//                    //render the collapse/expand animations as necessary
//                    searchView.swapSuggestions(results)
//
//                    //let the users know that the background
//                    //process has completed
//                    searchView.hideProgress()
//                }
            }

            Timber.d("onSearchTextChanged($newQuery)")
        })

        searchView.setOnSearchListener(
                object : FloatingSearchView.OnSearchListener {
                    override fun onSuggestionClicked(searchSuggestion: SearchSuggestion) {
                        lastQuery = searchSuggestion.body

                        Timber.d("onSuggestionClicked(), value: ${searchSuggestion.body}")
                    }

                    override fun onSearchAction(query: String) {
                        lastQuery = query

                        Timber.d("onSearchAction()")
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
                                searchView.swapSuggestions(DataHelper.getHistory(activity, 3))
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
                    //just print action
                    Toast.makeText(activity.applicationContext, item.title, Toast.LENGTH_SHORT).show()

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


//            if (colorSuggestion.isHistory) {
//                leftIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_history_black_24dp, null))
//                leftIcon.alpha = .36f
//            } else {
//                leftIcon.alpha = 0.0f
//                leftIcon.setImageDrawable(null)
//            }
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
}