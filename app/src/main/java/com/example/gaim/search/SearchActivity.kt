package com.example.gaim.search

import androidx.appcompat.app.AppCompatActivity
import com.example.gaim.ui.AbstractActivity
import com.example.gaim.ui.search.*

interface Named {
    val name: String
}

interface GaimActivity{
    val activity: Class<out AbstractActivity>
}

enum class SearchActivity (override val activity: Class<out AbstractActivity>): Named, GaimActivity{
    SURVEY(SurveySearchActivity::class.java),
    FREEFORM( FreeformSearchActivity::class.java),
    IMAGE(ImageSearchActivity::class.java);

    companion object {
        fun find(search: Class<out AbstractActivity>): SearchActivity? {
            val activity =SearchActivity.entries.find { it.activity == search };
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