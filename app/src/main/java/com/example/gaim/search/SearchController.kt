package com.example.gaim.search

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

//checks the intent for true search activities and runs them
class SearchController (private val context: Context, private val intent: Intent){
    //checks given intent for true search activies and runs them
    public fun start(){
        runSearches()

        TODO("Implement report generation (either call or in this class) after searches have been finished")
    }

    private fun runSearches(){
        for(activity in SearchActivity.entries){
            if(intent.extras?.getBoolean(activity.key) == SearchState.PENDING.value){
                context.startActivity(intent)
            }
        }
    }

    //sets the search activity as processed and goes to check if any more are still pending
    public fun complete(search: Class<out AppCompatActivity>){
        val activity = SearchActivity.find(search)

        intent.extras?.putBoolean(activity?.key, SearchState.PROCESSED.value); //indicates that the given key has been processed in the intent

        start()
    }
}