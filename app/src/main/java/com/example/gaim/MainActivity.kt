package com.example.gaim

import android.content.Intent
import com.example.gaim.ui.utility.SwitchButtonTracker
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton

import androidx.activity.enableEdgeToEdge
import com.example.gaim.search.SearchActivity
import com.example.gaim.search.SearchController
import com.example.gaim.search.SearchState
import com.example.gaim.ui.AbstractActivity

//Homepage
class MainActivity : AbstractActivity () {
    //hash map containing relevant search button activities to their button IDs
    private val searchButtonMap: Map<SearchActivity, Int> = hashMapOf(
        SearchActivity.SURVEY to R.id.survey_search,
        SearchActivity.FREEFORM to R.id.freeform_search,
        SearchActivity.IMAGE to R.id.image_search
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        //assigning buttons to values
        val homeButton = findViewById<ImageButton>(R.id.home_page)
        val accountButton = findViewById<ImageButton>(R.id.account_page)

        //set up search buttons
        val searchStateTrackers = setUpSearchButtons()

        //start button
        val startSearchButton = findViewById<Button>(R.id.start_search)

        startSearchButton.setOnClickListener {
            runSearch(searchStateTrackers)
        }
    }

    //sets up the search buttons to trackers in a collection
    private fun setUpSearchButtons(): Collection<SearchStateTracker>{
        var searchStateTrackers: MutableCollection<SearchStateTracker> = LinkedHashSet()

        for (entry in searchButtonMap){
            val searchButton = findViewById<Button>(entry.value)

            searchStateTrackers.add(SearchStateTracker(entry.key, searchButton))
        }

        return searchStateTrackers
    }

    //runs the search
    private fun runSearch(searchStateTrackers: Collection<SearchStateTracker>){
        for(tracker in searchStateTrackers){
            intent.putExtra(tracker.searchName(), tracker.state())
            Log.d("", tracker.searchName() + "\t" + tracker.state())
        }

        val searchController = SearchController(this, intent)

        searchController.start()
    }

    private fun run(intent: Intent){
        startActivity(intent)
    }
}

//Creates a tracker for a search state and associates a search activity
class SearchStateTracker(private val search: SearchActivity, button: Button) : SwitchButtonTracker<SearchState>(button) {
    override var state: SearchState = SearchState.PROCESSED;

    fun searchName(): String{
        return search.name
    }
}

