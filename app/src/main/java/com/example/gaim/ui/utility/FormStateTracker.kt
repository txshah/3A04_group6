package com.example.gaim.ui.utility

import android.content.Intent
import android.util.Log
import com.example.gaim.search.SearchActivity

class FormStateTracker(val activity: SearchActivity) {
    private var _state: ButtonState = ButtonState.UNSELECTED
    private var _searchResult: Intent? = null
    
    fun getState(): ButtonState {
        Log.d("FormStateTracker", "${activity.name} - Getting state: $_state")
        return _state
    }
    
    fun setState(newState: ButtonState) {
        Log.d("FormStateTracker", "${activity.name} - Changing state from $_state to $newState")
        _state = newState
    }

    fun setSearchResult(intent: Intent) {
        Log.d("FormStateTracker", "${activity.name} - Saving search result")
        _searchResult = intent
    }

    fun getSearchResult(): Intent? {
        Log.d("FormStateTracker", "${activity.name} - Getting search result")
        return _searchResult
    }
} 