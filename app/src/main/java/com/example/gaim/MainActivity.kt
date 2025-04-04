package com.example.gaim

import com.example.gaim.ui.utility.SwitchButtonTracker
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import com.example.gaim.search.GaimActivity
import com.example.gaim.search.SearchActivity
import com.example.gaim.search.SearchController
import com.example.gaim.search.SearchState
import com.example.gaim.ui.AbstractActivity
import com.example.gaim.ui.MainpageActivity
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.ProgressBar

//Homepage
class MainActivity : AbstractActivity () {
    private val imageButtonMap: Map<GaimActivity, Int> = hashMapOf(
        MainpageActivity.ACCOUNT to R.id.account_page,
        MainpageActivity.MAIN to R.id.home_page
    )

    //hash map containing relevant search button activities to their button IDs
    private val searchButtonMap: Map<SearchActivity, Int> = hashMapOf(
        SearchActivity.SURVEY to R.id.survey_search,
        SearchActivity.FREEFORM to R.id.freeform_search,
        SearchActivity.IMAGE to R.id.image_search
    )

    private val searchButtonID: Int = R.id.start_search;

    private var resultTextView: TextView? = null
    private var geminiService: GeminiService? = null
    private var geminiPromptInput: TextInputEditText? = null
    private var loadingIndicator: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        //assigning buttons to values
        setUpImageButtons()

        //set up search buttons
        val searchStateTrackers = setUpSearchButtons()

        //start button
        setUpStartSearchButton(searchStateTrackers)
        
        //Gemini test button
        setUpGeminiTestButton()

        resultTextView = findViewById(R.id.status)
        // Make the TextView scrollable
        resultTextView?.movementMethod = ScrollingMovementMethod()
        
        geminiPromptInput = findViewById(R.id.gemini_prompt_input)
        geminiService = GeminiService(this)
        loadingIndicator = findViewById(R.id.loading_indicator)
    }

    //sets up the image buttons in the top bar
    private fun setUpImageButtons() {
        for(entry in imageButtonMap){
            val button = findViewById<MaterialButton>(entry.value)

            button.setOnClickListener {
                nextActivity(entry.key)
            }
        }
    }

    //sets up the search buttons to trackers in a collection
    private fun setUpSearchButtons(): Collection<SearchStateTracker>{
        var searchStateTrackers: MutableCollection<SearchStateTracker> = LinkedHashSet()

        for (entry in searchButtonMap){
            val searchButton = findViewById<Button>(entry.value)

            searchStateTrackers.add(SearchStateTracker(entry.key, searchButton))
        }

        return searchStateTrackers
    }

    //sets up the button that starts the search
    private fun setUpStartSearchButton(searchStateTrackers: Collection<SearchStateTracker>){
        val startSearchButton = findViewById<Button>(searchButtonID)

        startSearchButton.setOnClickListener {
            runSearch(searchStateTrackers)
        }
    }

    //runs the search
    private fun runSearch(searchStateTrackers: Collection<SearchStateTracker>){
        for(tracker in searchStateTrackers){
            intent.putExtra(tracker.searchName(), tracker.state())
        }

        val searchController = SearchController(this, intent) //preparing search controller

        searchController.start()
    }

    //sets up the Gemini test button
    private fun setUpGeminiTestButton() {
        val geminiTestButton = findViewById<Button>(R.id.gemini_test)
        
        geminiTestButton.setOnClickListener {
            resultTextView?.text = "Sending request to Gemini..."
            
            // Show loading indicator
            loadingIndicator?.visibility = View.VISIBLE
            
            // Get the custom prompt from the input field
            val customPrompt = geminiPromptInput?.text?.toString()?.takeIf { it.isNotBlank() }
            
            // Pass the custom prompt to the testGemini method
            geminiService?.testGemini(
                modelName = "gemini-1.5-flash",
                onResult = { result ->
                    resultTextView?.text = result
                    // Hide loading indicator when response is received
                    loadingIndicator?.visibility = View.GONE
                },
                customPrompt = customPrompt
            )
        }
    }
}

//Creates a tracker for a search state and associates a search activity
class SearchStateTracker(private val search: SearchActivity, button: Button) : SwitchButtonTracker<SearchState>(button) {
    override var state: SearchState = SearchState.PROCESSED;

    fun searchName(): String{
        return search.name
    }
}

