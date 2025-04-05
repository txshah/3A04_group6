package com.example.gaim.ui.search

import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import com.example.gaim.R
import com.example.gaim.search.algorithm.SearchAlgorithm
import com.example.gaim.search.algorithm.SurveySearchAlgorithm

class SurveySearchActivity: AbstractSearchActivity<String> () {
    // Initialize algorithm lazily since we need context
    override val algorithm: SearchAlgorithm<String> by lazy { 
        SurveySearchAlgorithm(this)
    }

    private val submitID = R.id.submit_freeform
    private val legsID = R.id.spinner_legs
    private val coatID = R.id.spinner_coat
    private val colourID = R.id.spinner_colour
    private val domainID = R.id.spinner_domain
    private val regionID = R.id.spinner_region
    private val sizeID = R.id.spinner_size

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.survey_search)

        val submitButton = findViewById<Button>(submitID)
        val legsSpinner = findViewById<Spinner>(legsID)
        val coatSpinner = findViewById<Spinner>(coatID)
        val colourSpinner = findViewById<Spinner>(colourID)
        val domainSpinner = findViewById<Spinner>(domainID)
        val regionSpinner = findViewById<Spinner>(regionID)
        val sizeSpinner = findViewById<Spinner>(sizeID)

        submitButton.setOnClickListener {
            val surveyInput = buildSurveyInput(
                legsSpinner.selectedItem.toString(),
                coatSpinner.selectedItem.toString(),
                colourSpinner.selectedItem.toString(),
                domainSpinner.selectedItem.toString(),
                regionSpinner.selectedItem.toString(),
                sizeSpinner.selectedItem.toString()
            )
            completeSearch(surveyInput, this)
        }
    }

    private fun buildSurveyInput(
        legs: String,
        coat: String,
        colour: String,
        domain: String,
        region: String,
        size: String
    ): String {
        return "legs:$legs;coat:$coat;colour:$colour;domain:$domain;region:$region;size:$size"
    }
}