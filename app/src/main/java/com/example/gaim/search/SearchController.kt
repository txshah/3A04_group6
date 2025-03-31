package com.example.gaim.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.example.gaim.ui.AbstractActivity
import com.example.gaim.ui.search.AbstractSearchActivity

//checks the intent for true search activities and runs them
class SearchController (private val activity: AbstractActivity, private val intent: Intent){
    //checks given intent for true search activies and runs them
    public fun start(){
        runSearches()

        //to do("Implement report generation (either call or in this class) after searches have been finished")
    }

    private fun runSearches(){
        for(entry in SearchActivity.entries){
            if(intent.extras?.getBoolean(entry.name) == SearchState.PENDING.value){
                Log.d("TAG", "Starting activity: " + entry.name)
                val newIntent = Intent(this@SearchController.activity, entry.search);

                intent.extras?.let { newIntent.putExtras(it) }

                this.activity.nextActivity(entry.search, intent)
                Log.d("TAG", "Activity started")
            }
        }
    }

    //sets the search activity as processed and goes to check if any more are still pending
    public fun complete(search: Class<out AppCompatActivity>){
        val activity = SearchActivity.find(search)

        intent.extras?.putBoolean(activity?.name, SearchState.PROCESSED.value); //indicates that the given key has been processed in the intent

        start()
    }
}