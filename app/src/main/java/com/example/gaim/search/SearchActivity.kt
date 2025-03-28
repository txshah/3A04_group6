package com.example.gaim.search

import androidx.appcompat.app.AppCompatActivity
import com.example.gaim.ui.search.*

enum class SearchActivity (public val key: String, public val search: Class<out AppCompatActivity>) {
    SURVEY("survey", SurveySearchActivity::class.java),
    FREEFORM("freeform", FreeformSearchActivity::class.java),
    IMAGE("image", ImageSearchActivity::class.java);

    companion object {
        fun find(search: Class<out AppCompatActivity>): SearchActivity? {
            val activity =SearchActivity.entries.find { it.search == search };
            return activity
        }
    }
}

//pending indicates the search must be run, processed means that the search must no longer be run
enum class SearchState (public val value: Boolean){
    PENDING(true),
    PROCESSED(false);
}