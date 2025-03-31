package com.example.gaim.ui.search

import android.os.Bundle
import com.example.gaim.search.Survey
import com.example.gaim.search.algorithm.SearchAlgorithm
import com.example.gaim.search.algorithm.SurveySearchAlgorithm

class SurveySearchActivity: AbstractSearchActivity<Survey> () {
    override val algorithm: SearchAlgorithm<Survey> = SurveySearchAlgorithm();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TODO("Not yet implemented")
    }
}