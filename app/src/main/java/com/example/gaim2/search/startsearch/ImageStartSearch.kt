package com.example.gaim2.search.startsearch

import com.example.gaim2.search.algorithm.SearchAlgorithm
import com.example.gaim2.ui.theme.SearchPage

//see StartSearch for description, Image based input
class ImageStartSearch : StartSearch<String>() {
    override val searchAlgorithm: SearchAlgorithm<String>
        get() = TODO("Not yet implemented")
    override val searchUI: SearchPage
        get() = TODO("Not yet implemented")

    override fun runDisplay(): String {
        TODO("Not yet implemented")
    }
}