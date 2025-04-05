package com.example.gaim.ui.search

import android.content.ComponentName
import android.util.Log
import com.example.gaim.search.SearchController
import com.example.gaim.search.algorithm.SearchAlgorithm
import com.example.gaim.ui.AbstractActivity

abstract class AbstractSearchActivity <T>: AbstractActivity ()  {
    protected abstract val algorithm: SearchAlgorithm<T>;

    //run this function when the form has all required information
    //and should be saved (but not start the search yet)
    protected fun completeSearch(input: T, inheritor: AbstractSearchActivity<T>) {
        Log.d("AbstractSearchActivity", "Starting completeSearch for ${inheritor.javaClass.simpleName}")
        val searchResult = algorithm.search(input)
        searchResult.putInIntent(intent, inheritor.javaClass)
        
        // Set the component name on the intent to ensure proper activity result handling
        intent.component = ComponentName(this, inheritor.javaClass)
        
        Log.d("AbstractSearchActivity", "Setting result OK and finishing activity")
        // Mark this form as completed but don't start the search process
        setResult(RESULT_OK, intent)
        finish()
    }
}