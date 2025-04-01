package com.example.gaim.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.gaim.ui.AbstractActivity

//checks the intent for true search activities and runs them
class SearchController (private val activity: AbstractActivity, private val intent: Intent){
    //checks given intent for true search activies and runs them
    fun start(){
        runSearches()

        //to do("Implement report generation (either call or in this class) after searches have been finished")
    }

    private fun runSearches(){
        for(entry in SearchActivity.entries){
            if(intent.extras?.getBoolean(entry.name) == SearchState.PENDING.value){
                val newIntent = Intent(this@SearchController.activity, entry.activity);

                intent.extras?.let { newIntent.putExtras(it) }

                this.activity.nextActivity(entry, intent)
            }
        }
    }

    //sets the search activity as processed and goes to check if any more are still pending
    fun complete(search: Class<out AbstractActivity>){
        val activity = SearchActivity.find(search)

        intent.extras?.putBoolean(activity?.name, SearchState.PROCESSED.value); //indicates that the given key has been processed in the intent

        start()
    }
}