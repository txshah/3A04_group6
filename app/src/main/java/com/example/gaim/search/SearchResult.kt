package com.example.gaim.search

import android.content.Intent
import com.example.gaim.ui.AbstractActivity
import java.io.Serializable

//
//class SearchResult(private val animalName: String, private val accuracy: Double) {
////store all three search results and send to controllor
//
//}
//to store data = data class
data class SearchResult(val name: String?, val accuracy: Double?) : Serializable {
    fun putInIntent(intent: Intent, activity: Class<out AbstractActivity>){
        val searchActivity = SearchActivity.find(activity)
        searchActivity?.let { putInIntent(intent, it) }
    }

    fun putInIntent(intent: Intent, activity: SearchActivity){
        val name = toName(activity)
        var accuracy = toAccuracy(activity)

        intent.putExtra(name, this.name)
        intent.putExtra(accuracy, this.accuracy)
    }

    companion object{
        private const val serialVersionUID = 1L

        fun getFromIntent(intent: Intent, activity: Class<out AbstractActivity>){
            val searchActivity = SearchActivity.find(activity)
            searchActivity?.let { getFromIntent(intent, it) }
        }

        fun getFromIntent(intent: Intent, activity: SearchActivity): SearchResult{
            val name = intent.extras?.getString(toName(activity))
            val accuracy = intent.extras?.getDouble(toAccuracy(activity))

            return SearchResult(name, accuracy)
        }

        fun toName(activity: SearchActivity): String{
            return activity.name + ": name"
        }

        fun toAccuracy(activity: SearchActivity): String{
            return activity.name + ": accuracy"
        }
    }
}