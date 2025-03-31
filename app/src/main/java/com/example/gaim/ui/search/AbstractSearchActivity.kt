package com.example.gaim.ui.search

import androidx.appcompat.app.AppCompatActivity
import com.example.gaim.search.SearchController
import com.example.gaim.search.algorithm.SearchAlgorithm
import com.example.gaim.ui.AbstractActivity

abstract class AbstractSearchActivity <T>: AbstractActivity ()  {
    protected abstract val algorithm: SearchAlgorithm<T>;

    //run this function when the freeform search has all
    //required information and the page can move to the
    // next search type pass in "this" in inheritor classes
    protected fun completeSearch(inheritor: AbstractSearchActivity<T>){
        val searchController = SearchController(inheritor, intent)
        searchController.complete(inheritor.javaClass)
    }
}