package com.example.gaim.search.startsearch

import com.example.gaim.search.Survey
import com.example.gaim.search.algorithm.SearchAlgorithm

//see StartSearch for description, Survey based input
class SurveyStartSearch : StartSearch<Survey>() {
    override val searchAlgorithm: SearchAlgorithm<Survey>
        get() = TODO("Not yet implemented")

    override fun runDisplay(): Survey {
        TODO("Not yet implemented")
    }

}