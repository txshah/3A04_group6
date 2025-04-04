package com.example.gaim.search

import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import com.example.gaim.ui.AbstractActivity
import com.example.gaim.ui.search.AbstractSearchActivity

//
//class SearchResult(private val animalName: String, private val accuracy: Double) {
////store all three search results and send to controllor
//
//}
//to store data = data class
data class SearchResult(val name: String?, val accuracy: Double?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeValue(accuracy)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchResult> {
        override fun createFromParcel(parcel: Parcel): SearchResult {
            return SearchResult(parcel)
        }

        override fun newArray(size: Int): Array<SearchResult?> {
            return arrayOfNulls(size)
        }

        fun getFromIntent(intent: Intent, activity: Class<out AbstractActivity>) {
            val searchActivity = SearchActivity.find(activity)
            searchActivity?.let { getFromIntent(intent, it) }
        }

        fun getFromIntent(intent: Intent, activity: SearchActivity): SearchResult {
            val name = intent.extras?.getString(toName(activity))
            val accuracy = intent.extras?.getDouble(toAccuracy(activity))
            return SearchResult(name, accuracy)
        }

        fun toName(activity: SearchActivity): String {
            return activity.name + ": name"
        }

        fun toAccuracy(activity: SearchActivity): String {
            return activity.name + ": accuracy"
        }
    }

    fun putInIntent(intent: Intent, activity: Class<out AbstractActivity>) {
        val searchActivity = SearchActivity.find(activity)
        searchActivity?.let { putInIntent(intent, it) }
    }

    fun putInIntent(intent: Intent, activity: SearchActivity) {
        val name = toName(activity)
        var accuracy = toAccuracy(activity)
        intent.putExtra(name, this.name)
        intent.putExtra(accuracy, this.accuracy)
    }
}