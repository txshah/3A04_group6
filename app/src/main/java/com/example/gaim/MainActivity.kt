package com.example.gaim

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.gaim.search.Opposable
import com.example.gaim.search.SearchActivity
import com.example.gaim.search.SearchState

//Homepage
class MainActivity : AppCompatActivity () {
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
        //top bar buttons
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

    private fun setUpSearchButtons(): Collection<SearchStateTracker>{
        var searchStateTrackers: MutableCollection<SearchStateTracker> = LinkedHashSet()

        for (entry in searchButtonMap){
            val searchButton = findViewById<Button>(entry.value)
            searchStateTrackers.add(SearchStateTracker(entry.key, searchButton))

        }

        return searchStateTrackers
    }

    private fun runSearch(searchStateTrackers: Collection<SearchStateTracker>){
        for(tracker in searchStateTrackers){
            intent.putExtra(tracker.searchName(), tracker.state())
        }
    }
}

//Creates a tracker for a search state and associates a search activity
class SearchStateTracker(private val search: SearchActivity, button: Button) : SwitchButtonTracker<SearchState>(button) {
    override var state: SearchState = SearchState.PROCESSED;

    fun searchName(): String{
        return search.name
    }
}

//creates a switch button that tracks the button state via an opposable class (enum)
abstract class SwitchButtonTracker<K : Opposable<K>>(button: Button){
    protected abstract var state: K;

    init{
        button.setOnClickListener {
            state = state.opposite()
        }
    }

    fun state(): Boolean{ //makes state read only (val vs var)
        return state.value
    }
}