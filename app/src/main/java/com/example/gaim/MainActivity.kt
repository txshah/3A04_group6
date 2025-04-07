package com.example.gaim

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import com.example.gaim.search.GaimActivity
import com.example.gaim.search.SearchActivity
import com.example.gaim.search.SearchController
import com.example.gaim.ui.AbstractActivity
import com.example.gaim.ui.MainpageActivity
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView
import android.text.method.ScrollingMovementMethod
import android.widget.ProgressBar
import com.example.gaim.ui.utility.ButtonState
import com.example.gaim.ui.utility.FormStateTracker
import android.content.Intent
import android.util.Log
import android.content.ComponentName

//Homepage
class MainActivity : AbstractActivity() {
//    set up account and main homepage button
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
//buttons
    private val searchButtonID: Int = R.id.start_search
    private val fillFormsButtonID: Int = R.id.fill_forms
//views
    private var resultTextView: TextView? = null
    private var geminiService: GeminiService? = null
//    testing views
    private var geminiPromptInput: TextInputEditText? = null
    private var loadingIndicator: ProgressBar? = null

    // Track which forms have been visited
    private val visitedForms = mutableSetOf<String>()

    // Store formStateTrackers as a class property
    private lateinit var formStateTrackers: Map<SearchActivity, FormStateTracker>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Clear visited forms when activity is created
        visitedForms.clear()

        //assigning buttons to values
        setUpImageButtons()

        //set up search buttons and store the trackers
        formStateTrackers = setUpSearchButtons()

        //fill forms button
        setUpFillFormsButton()

        //start button
        setUpStartSearchButton()

        // Make the TextView scrollable
        resultTextView?.movementMethod = ScrollingMovementMethod()

        geminiService = GeminiService(this)
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
    private fun setUpSearchButtons(): Map<SearchActivity, FormStateTracker> {
        Log.d("MainActivity", "Setting up search buttons")
        return searchButtonMap.mapValues { (activity, buttonId) ->
            val searchButton = findViewById<Button>(buttonId)
            val tracker = FormStateTracker(activity)
            Log.d("MainActivity", "Created tracker for ${activity.name} with initial state: ${tracker.getState()}")

            // Set up click listener for each search button
            searchButton.setOnClickListener {
                Log.d("MainActivity", "Button clicked for ${activity.name}, current state: ${tracker.getState()}")
                when (tracker.getState()) {
                    ButtonState.PROCESSED -> {
                        Log.d("MainActivity", "${activity.name} is PROCESSED, showing edit dialog")
//                        box for editing form - pop up
                        AlertDialog.Builder(this)
                            .setTitle("Edit Form?")
                            .setMessage("Would you like to edit this form again?")
                            .setPositiveButton("Yes") { _, _ ->
                                nextActivity(activity)
                            }
                            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                            .show()
                    }
//                    button state select and unselct
                    ButtonState.PENDING -> {
                        Log.d("MainActivity", "Setting ${activity.name} to UNSELECTED")
                        tracker.setState(ButtonState.UNSELECTED)
                        searchButton.isSelected = false
                    }
                    ButtonState.UNSELECTED -> {
                        Log.d("MainActivity", "Setting ${activity.name} to PENDING")
                        tracker.setState(ButtonState.PENDING)
                        searchButton.isSelected = true
                    }
                }
            }
//tracker - so we can view users past searches
            tracker
        }
    }

    //sets up the button that starts filling in forms
    private fun setUpFillFormsButton() {
        val fillFormsButton = findViewById<Button>(fillFormsButtonID)

        fillFormsButton.setOnClickListener {
            Log.d("MainActivity", "Fill Forms button clicked")
            // Get all selected but unfilled forms
            val pendingForms = formStateTrackers.entries
                .filter { it.value.getState() == ButtonState.PENDING }
                .map { it.key }

            Log.d("MainActivity", "Pending forms: ${pendingForms.map { it.name }}")

            if (pendingForms.isEmpty()) {
                // No pending forms, check if we have any processed forms
                val processedForms = formStateTrackers.entries
                    .filter { it.value.getState() == ButtonState.PROCESSED }
                    .map { it.key }

                Log.d("MainActivity", "Processed forms: ${processedForms.map { it.name }}")
//if empty
                if (processedForms.isEmpty()) {
                    Log.d("MainActivity", "No forms selected at all")
                    AlertDialog.Builder(this)
                        .setTitle("No Forms Selected")
                        .setMessage("Please select at least one form to fill in.")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                } else {
                    Log.d("MainActivity", "All forms are filled")
                    AlertDialog.Builder(this)
                        .setTitle("All Forms Filled")
                        .setMessage("All selected forms have been filled. Would you like to start the search or edit existing forms?")
                        .setPositiveButton("Start Search") { _, _ -> runSearch() }
                        .setNegativeButton("Edit Forms") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
                return@setOnClickListener
            }

            // Show all pending forms that need to be filled
            val formNames = pendingForms.map { it.name }.toTypedArray()
            Log.d("MainActivity", "Starting to fill forms: ${formNames.joinToString()}")
            AlertDialog.Builder(this)
                .setTitle("Forms to Fill")
                .setMessage("You need to fill in the following forms:\n" + formNames.joinToString("\n"))
                .setPositiveButton("Start Filling") { _, _ ->
                    // Start with the first form
                    Log.d("MainActivity", "Starting first form: ${pendingForms[0].name}")
                    nextActivity(pendingForms[0], intent)
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    //sets up the button that starts the search
    private fun setUpStartSearchButton() {
        val startSearchButton = findViewById<Button>(searchButtonID)

        startSearchButton.setOnClickListener {
            Log.d("MainActivity", "Start Search button clicked")
            // Get forms in different states
            val pendingForms = formStateTrackers.entries
                .filter { it.value.getState() == ButtonState.PENDING }
                .map { it.key }

            val processedForms = formStateTrackers.entries
                .filter { it.value.getState() == ButtonState.PROCESSED }
                .map { it.key }

            Log.d("MainActivity", "Current form states - Pending: ${pendingForms.map { it.name }}, Processed: ${processedForms.map { it.name }}")

            when {
                pendingForms.isNotEmpty() -> {
                    Log.d("MainActivity", "Cannot start search - have unfilled forms: ${pendingForms.map { it.name }}")
                    val formNames = pendingForms.map { it.name }.toTypedArray()
//                    pop up
                    AlertDialog.Builder(this)
                        .setTitle("Unfilled Forms")
                        .setMessage("You must fill in all selected forms before starting the search:\n" + formNames.joinToString("\n"))
                        .setPositiveButton("Fill Forms") { _, _ ->
                            Log.d("MainActivity", "Starting to fill first pending form: ${pendingForms[0].name}")
                            nextActivity(pendingForms[0], intent)
                        }
                        .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
                processedForms.isEmpty() -> {
                    Log.d("MainActivity", "Cannot start search - no forms selected")
                    AlertDialog.Builder(this)
                        .setTitle("No Forms Selected")
                        .setMessage("Please select and fill in at least one form before starting the search.")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
                else -> {
                    Log.d("MainActivity", "All forms are processed, starting search with: ${processedForms.map { it.name }}")
                    runSearch()
                }
            }
        }
    }

    //runs the search
    private fun runSearch() {
        Log.d("MainActivity", "Running search")
        // Include both the processed state and search results in the intent
        for((activity, tracker) in formStateTrackers) {
//            for eacch acitvity tracker - processed buttons
            val isProcessed = tracker.getState() == ButtonState.PROCESSED
            Log.d("MainActivity", "Form ${activity.name} is processed: $isProcessed")
            intent.putExtra(activity.name, isProcessed)

            // If the form is processed, include its search result
            if (isProcessed) {
                tracker.getSearchResult()?.let { searchIntent ->
                    // Copy all the extras from the search result intent to our intent
                    searchIntent.extras?.let { bundle ->
                        intent.putExtras(bundle)
                    }
                }
            }
        }
// call search controller
        Log.d("MainActivity", "Starting SearchController")
        val searchController = SearchController(this, intent)
        searchController.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("MainActivity", "onActivityResult called with requestCode: $requestCode, resultCode: $resultCode")

        if (resultCode == RESULT_OK && data != null) {
            // Get the activity that just completed
            val completedClassName = data.component?.className
            Log.d("MainActivity", "Completed activity class name: $completedClassName")

            val completedActivity = SearchActivity.entries.find { searchActivity ->
                val matches = completedClassName?.endsWith(searchActivity.activity.simpleName) == true
                Log.d("MainActivity", "Checking ${searchActivity.name} (${searchActivity.activity.simpleName}) against $completedClassName: $matches")
                matches
            }

            Log.d("MainActivity", "Found completed activity: ${completedActivity?.name}")

            completedActivity?.let { activity ->
                // Mark this form as processed and save its search result
                val tracker = formStateTrackers[activity]
                Log.d("MainActivity", "Current state for ${activity.name}: ${tracker?.getState()}")
                tracker?.setState(ButtonState.PROCESSED)
                // Save the search result from the returned intent
                tracker?.setSearchResult(data)
                Log.d("MainActivity", "Updated state for ${activity.name}: ${tracker?.getState()}")

                // Find the next pending form
                val nextPendingForm = formStateTrackers.entries
                    .find { it.value.getState() == ButtonState.PENDING }?.key

                Log.d("MainActivity", "Next pending form: ${nextPendingForm?.name}")

                if (nextPendingForm != null) {
                    // Automatically start the next form
                    nextActivity(nextPendingForm, intent)
                } else {
                    // All forms are completed, show completion dialog
                    AlertDialog.Builder(this)
                        .setTitle("Forms Completed")
                        .setMessage("All selected forms have been completed. You can now start the search.")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            } ?: run {
                Log.e("MainActivity", "Could not find matching activity for $completedClassName")
            }
        } else {
            Log.d("MainActivity", "Form was not completed successfully")
        }
    }

    override fun onResume() {
        super.onResume()
        // Remove the incorrect form state tracking from onResume
    }

    //starts the next activity
    fun nextActivity(activity: GaimActivity) {
        Log.d("MainActivity", "Starting next activity: ${activity.activity.simpleName}")
        val newIntent = Intent(this, activity.activity)
        newIntent.putExtras(intent)

        if (activity is SearchActivity) {
            Log.d("MainActivity", "Starting activity for result")
            // Add component name to intent for proper activity result handling
            newIntent.component = ComponentName(this, activity.activity)
            startActivityForResult(newIntent, 1)
        } else {
            Log.d("MainActivity", "Starting activity normally")
            startActivity(newIntent)
        }
    }

    override fun nextActivity(activity: GaimActivity, intent: Intent) {
        Log.d("MainActivity", "Starting next activity with intent: ${activity.activity.simpleName}")
        // Add component name to intent for proper activity result handling
        intent.component = ComponentName(this, activity.activity)
        if (activity is SearchActivity) {
            Log.d("MainActivity", "Starting activity for result with intent")
            startActivityForResult(intent, 1)
        } else {
            Log.d("MainActivity", "Starting activity normally with intent")
            startActivity(intent)
        }
    }
}