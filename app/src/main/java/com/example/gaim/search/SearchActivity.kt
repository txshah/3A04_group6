package com.example.gaim.search

import androidx.appcompat.app.AppCompatActivity
import com.example.gaim.ui.search.*

interface Named {
    val name: String
}

enum class SearchActivity (val search: Class<out AppCompatActivity>): Named{
    SURVEY(SurveySearchActivity::class.java),
    FREEFORM( FreeformSearchActivity::class.java),
    IMAGE(ImageSearchActivity::class.java);

    companion object {
        fun find(search: Class<out AppCompatActivity>): SearchActivity? {
            val activity =SearchActivity.entries.find { it.search == search };
            return activity
        }
    }
}

interface Opposable<T>{
    val value: Boolean
    fun opposite(): T
}

//pending indicates the search must be run, processed means that the search must no longer be run
enum class SearchState (override val value: Boolean) : Opposable<SearchState>{
    PENDING(true),
    PROCESSED(false);

    companion object {
        fun find(value: Boolean): SearchState? {
            val state = SearchState.entries.find { it.value == value };
            return state
        }
    }

    override fun opposite(): SearchState {
        return find(!this.value) !!
    }
}