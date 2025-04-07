package com.example.gaim.ui.search

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.gaim.R
import com.example.gaim.search.SearchResult
import androidx.core.content.ContextCompat
import android.content.Intent
import android.widget.Button
import com.example.gaim.ui.AbstractActivity
import com.example.gaim.ui.MainpageActivity

class DisplayResultsActivity : AbstractActivity() {
    private var container: LinearLayout? = null
    private val TAG = "DisplayResultsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate started")
        
        try {
            setContentView(R.layout.displayresults)
            Log.d(TAG, "setContentView successful")

            container = findViewById(R.id.otherMatchesContainer)
            Log.d(TAG, "Container found: ${container != null}")

            // Get search results from the intent
            val searchResults = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayListExtra("search_results", SearchResult::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableArrayListExtra<SearchResult>("search_results")
            } ?: ArrayList()
            
            Log.d(TAG, "Search results size: ${searchResults.size}")

            val backButton = findViewById<Button>(R.id.backButton)
            // Set up back button
            backButton.setOnClickListener {
                nextActivity(MainpageActivity.MAIN, intent)
            }

            if (searchResults.isNotEmpty()) {
                // Display best match
                val bestMatch = searchResults.maxByOrNull { it.accuracy ?: 0.0 }
                Log.d(TAG, "Best match found: ${bestMatch?.name}")
                bestMatch?.let { displayBestMatch(it) }

                // Display other matches
                val otherMatches = searchResults.filter { it != bestMatch }
                Log.d(TAG, "Other matches size: ${otherMatches.size}")
                displayOtherMatches(otherMatches)
            } else {
                Log.d(TAG, "No search results found")
            }

            // Set up generate report button
            findViewById<android.widget.Button>(R.id.generate_report)?.setOnClickListener {
                generateReport(searchResults)
            }
            Log.d(TAG, "onCreate completed successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
        }
    }

    private fun displayBestMatch(result: SearchResult) {
        try {
            findViewById<TextView>(R.id.bestMatchName)?.text = result.name ?: "Unknown"
            findViewById<TextView>(R.id.bestMatchAccuracy)?.text = 
                String.format("Accuracy: %.1f%%", (result.accuracy ?: 0.0) * 100)
            Log.d(TAG, "Best match displayed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying best match", e)
        }
    }

    private fun displayOtherMatches(results: List<SearchResult>) {
        try {
            container?.let { safeContainer ->
                safeContainer.removeAllViews() // Clear any existing views
                
                results.forEach { result ->
                    // Create a CardView programmatically
                    val card = CardView(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 0, 0, 32)
                        }
                        radius = 16f
                        elevation = 8f
                        setContentPadding(32, 32, 32, 32)
                        setCardBackgroundColor(ContextCompat.getColor(this@DisplayResultsActivity, R.color.white))
                    }

                    // Create the content layout
                    val content = LinearLayout(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        orientation = LinearLayout.VERTICAL
                    }

                    // Create and add name TextView
                    val nameView = TextView(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        text = result.name ?: "Unknown"
                        textSize = 16f
                        setTextColor(ContextCompat.getColor(this@DisplayResultsActivity, R.color.black))
                    }
                    content.addView(nameView)

                    // Create and add accuracy TextView
                    val accuracyView = TextView(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            topMargin = 8
                        }
                        text = String.format("Accuracy: %.1f%%", (result.accuracy ?: 0.0) * 100)
                        textSize = 14f
                        setTextColor(ContextCompat.getColor(this@DisplayResultsActivity, android.R.color.darker_gray))
                    }
                    content.addView(accuracyView)

                    // Add content to card and card to container
                    card.addView(content)
                    safeContainer.addView(card)
                }
                Log.d(TAG, "Other matches displayed successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying other matches", e)
        }
    }

    private fun generateReport(results: List<SearchResult>) {
        try {
            val bestMatch = results.maxByOrNull { it.accuracy ?: 0.0 }
            val animalName = bestMatch?.name ?: "Unknown"
            
            Log.d(TAG, "Generating report for animal: $animalName")
            
            // Launch the AnimalReportActivity
            val intent = Intent(this, AnimalReportActivity::class.java).apply {
                putExtra("animal_name", animalName)
            }
            startActivity(intent)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error generating report", e)
            android.widget.Toast.makeText(
                this,
                "Error generating report",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {
        container = null
        super.onDestroy()
        Log.d(TAG, "Activity destroyed")
    }
} 