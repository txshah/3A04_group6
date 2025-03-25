package com.example.gaim2.search.startsearch

import com.example.gaim2.search.Survey
import com.example.gaim2.search.algorithm.SearchAlgorithm
import com.example.gaim2.ui.theme.SearchPage

//see StartSearch for description, Survey based input
class SurveyStartSearch : StartSearch<Survey>() {
    override val searchAlgorithm: SearchAlgorithm<Survey>
        get() = TODO("Not yet implemented")
    override val searchUI: SearchPage
        get() = TODO("Not yet implemented")

    override fun runDisplay(): Survey {
        TODO("Not yet implemented")
    }

}