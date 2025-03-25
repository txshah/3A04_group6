package com.example.gaim2.search.startsearch

import com.example.gaim2.search.algorithm.SearchAlgorithm
import com.example.gaim2.ui.page.search_page.FreeformSearchPage

//see StartSearch for description, Freeform based input
class FreeformStartSearch : StartSearch<String> (){
    override val searchAlgorithm: SearchAlgorithm<String>
        get() = TODO("Not yet implemented")
    override val searchUI: FreeformSearchPage
        get() = TODO("Not yet implemented")

    override fun runDisplay(): String {
        TODO("Not yet implemented")
    }
}