package com.example.gaim.search

import android.content.Intent
import android.util.Log
import com.example.gaim.ui.AbstractActivity
import com.example.gaim.ui.search.DisplayResultsActivity

//checks the intent for true search activities and runs them
class SearchController (private val activity: AbstractActivity, private val intent: Intent){
    //checks given intent for true search activities and runs them
    private val searchResults = mutableListOf<SearchResult>()
    private val TAG = "SearchController"

    fun start(){
        Log.d(TAG, "Starting search process")
        runSearches()
//for all entries
        for(entry in SearchActivity.entries){
//            log statements for testing
            Log.d(TAG, "Processing entry: ${entry.name}")
            SearchResult.getFromIntent(intent, entry)?.let { result ->
                Log.d(TAG, "Found result for ${entry.name}: ${result.name} with accuracy ${result.accuracy}")
                addSearchResult(result)
            }
        }

        // Show results if we have any
        if (searchResults.isNotEmpty()) {
            Log.d(TAG, "Found ${searchResults.size} results, launching DisplayResultsActivity")
            val resultsIntent = Intent(activity, DisplayResultsActivity::class.java).apply {
                putParcelableArrayListExtra("search_results", ArrayList(searchResults))
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
//results updatex
            resultsIntent.putExtras(intent)
            activity.startActivity(resultsIntent)
        } else {
            Log.d(TAG, "No search results found")
        }
    }

    private fun runSearches(){
        Log.d(TAG, "Running searches")
//        go through all entries and based on that create intents to pass around data
        for(entry in SearchActivity.entries){
            if(intent.extras?.getBoolean(entry.name) == SearchState.PENDING.value){
                Log.d(TAG, "Found pending search for ${entry.name}")
                val newIntent = Intent(this@SearchController.activity, entry.activity).apply {
                    intent.extras?.let { putExtras(it) }
                }

                this.activity.nextActivity(entry, newIntent)
            }
        }
    }

    //sets the search activity as processed and goes to check if any more are still pending
    fun complete(search: Class<out AbstractActivity>){
        val activity = SearchActivity.find(search)
        activity?.let {
            Log.d(TAG, "Completing search for ${it.name}")
            intent.extras?.putBoolean(it.name, SearchState.PROCESSED.value)
        }
        start()
    }

    fun addSearchResult(result: SearchResult) {
//        confirming if values of same name exist
        val existingResult = searchResults.firstOrNull { it.name == result.name }
//         making sure unknown values not updated/used
        if (result.name == null) {
            Log.d(TAG, "Unknown skipped")
            return
        }
//not null existing value then update accuracy score more (because two agents give same result)
        if (existingResult != null) {
            Log.d(TAG, "Updating existing result for ${result.name}")
            searchResults.remove(existingResult)
            searchResults.add(
                existingResult.copy(accuracy = existingResult.accuracy?.plus(0.20))
            )
        } else {
//            give accuracy score
            Log.d(TAG, "Adding new result for ${result.name}")
            searchResults.add(result)
        }
        Log.d(TAG, "Search Result ${searchResults}")
    }

    /*
    fun final(): SearchResult? {
        if (searchResults.isEmpty()) return null

        return searchResults.maxByOrNull { it.accuracy }
    }

    */

    fun getResults(): List<SearchResult> {
        return searchResults.toList()
    }
}