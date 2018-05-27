package app.weatherhistory.weatherhistory.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import app.weatherhistory.weatherhistory.R
import app.weatherhistory.weatherhistory.data.ColorSuggestion
import app.weatherhistory.weatherhistory.data.DataHelper
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.arlib.floatingsearchview.util.Util

class SlidingSearchViewExampleFragment : BaseFragment() {

    private val TAG = "fragtag"
    private lateinit var mSearchView: FloatingSearchView
    private var mLastQuery = ""
    private var mDimDrawable: ColorDrawable? = null
    private val ANIM_DURATION: Long = 350
    val FIND_SUGGESTION_SIMULATED_DELAY: Long = 250
    private var mIsDarkSearchTheme = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_sliding_search_example, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mSearchView = view?.findViewById(R.id.floating_search_view) as FloatingSearchView

        setupFloatingSearch()
        setupDrawer()
    }

    private fun setupDrawer() = attachSearchViewActivityDrawer(mSearchView)

    private fun setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener(FloatingSearchView.OnQueryChangeListener { oldQuery, newQuery ->
            if (oldQuery != "" && newQuery == "") {
                mSearchView.clearSuggestions()
            } else {

                //this shows the top left circular progress
                //you can call it where ever you want, but
                //it makes sense to do it when loading something in
                //the background.
                mSearchView.showProgress()

                //simulates a query call to a data source
                //with a new query.
                DataHelper.findSuggestions(activity, newQuery, 5,
                        FIND_SUGGESTION_SIMULATED_DELAY) { results ->
                    //this will swap the data and
                    //render the collapse/expand animations as necessary
                    mSearchView.swapSuggestions(results)

                    //let the users know that the background
                    //process has completed
                    mSearchView.hideProgress()
                }
            }

            Log.d(TAG, "onSearchTextChanged()")
        })

        mSearchView.setOnSearchListener(object : FloatingSearchView.OnSearchListener {
            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion) {

                mLastQuery = searchSuggestion.body
            }

            override fun onSearchAction(query: String) {
                mLastQuery = query

                Log.d(TAG, "onSearchAction()")
            }
        })

        mSearchView.setOnFocusChangeListener(object : FloatingSearchView.OnFocusChangeListener {
            override fun onFocus() {
                val headerHeight = resources.getDimensionPixelOffset(R.dimen.sliding_search_view_header_height)

                val anim = ObjectAnimator.ofFloat(mSearchView, "translationY",
                        headerHeight.toFloat(), 0f)
                anim.setDuration(350)
                fadeDimBackground(0, 150, null)
                anim.addListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {
                        //show suggestions when search bar gains focus (typically history suggestions)
                        mSearchView.swapSuggestions(DataHelper.getHistory(activity, 3))

                    }
                })
                anim.start()

                Log.d(TAG, "onFocus()")
            }

            override fun onFocusCleared() {
                val headerHeight = resources.getDimensionPixelOffset(R.dimen.sliding_search_view_header_height)
                val anim = ObjectAnimator.ofFloat(mSearchView, "translationY",
                        0f, headerHeight.toFloat())
                anim.setDuration(350)
                anim.start()
                fadeDimBackground(150, 0, null)

                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchBarTitle(mLastQuery)

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());

                Log.d(TAG, "onFocusCleared()")
            }
        })


        //handle menu clicks the same way as you would
        //in a regular activity
        mSearchView.setOnMenuItemClickListener(FloatingSearchView.OnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_change_colors) {

                mIsDarkSearchTheme = true

                //demonstrate setting colors for items
                mSearchView.setBackgroundColor(Color.parseColor("#787878"))
                mSearchView.setViewTextColor(Color.parseColor("#e9e9e9"))
                mSearchView.setHintTextColor(Color.parseColor("#e9e9e9"))
                mSearchView.setActionMenuOverflowColor(Color.parseColor("#e9e9e9"))
                mSearchView.setMenuItemIconColor(Color.parseColor("#e9e9e9"))
                mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"))
                mSearchView.setClearBtnColor(Color.parseColor("#e9e9e9"))
                mSearchView.setDividerColor(Color.parseColor("#BEBEBE"))
                mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"))
            } else {

                //just print action
                Toast.makeText(activity.applicationContext, item.title,
                        Toast.LENGTH_SHORT).show()
            }
        })

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchView.setOnHomeActionClickListener(FloatingSearchView.OnHomeActionClickListener { Log.d(TAG, "onHomeClicked()") })

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
        mSearchView.setOnBindSuggestionCallback(SearchSuggestionsAdapter.OnBindSuggestionCallback { suggestionView, leftIcon, textView, item, itemPosition ->
            val colorSuggestion = item as ColorSuggestion

            val textColor = if (mIsDarkSearchTheme) "#ffffff" else "#000000"
            val textLight = if (mIsDarkSearchTheme) "#bfbfbf" else "#787878"

            if (colorSuggestion.getIsHistory()) {
                leftIcon.setImageDrawable(ResourcesCompat.getDrawable(resources,
                        R.drawable.ic_history_black_24dp, null))

                Util.setIconColor(leftIcon, Color.parseColor(textColor))
                leftIcon.alpha = .36f
            } else {
                leftIcon.alpha = 0.0f
                leftIcon.setImageDrawable(null)
            }

            textView.setTextColor(Color.parseColor(textColor))
            val text = colorSuggestion.getBody()
                    .replaceFirst(mSearchView.getQuery(),
                            "<font color=\"" + textLight + "\">" + mSearchView.getQuery() + "</font>")
            textView.text = Html.fromHtml(text)
        })

        /*
         * When the user types some text into the search field, a clear button (and 'x' to the
         * right) of the search text is shown.
         *
         * This listener provides a callback for when this button is clicked.
         */
        mSearchView.setOnClearSearchActionListener(FloatingSearchView.OnClearSearchActionListener { Log.d(TAG, "onClearSearchClicked()") })
    }

    private fun fadeDimBackground(from: Int, to: Int, listener: Animator.AnimatorListener?) {
        val anim = ValueAnimator.ofInt(from, to)
        anim.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            mDimDrawable?.alpha = value
        }
        if (listener != null) {
            anim.addListener(listener)
        }
        anim.duration = ANIM_DURATION
        anim.start()
    }
}