package com.example.gaim.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.gaim.R
import com.example.gaim.search.SearchResult

class DisplayResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.displayresults)

        // Get search results from the intent
        val searchResults = intent.getSerializableExtra("search_results") as? ArrayList<SearchResult> ?: ArrayList()

        if (searchResults.isNotEmpty()) {
            // Display best match
            val bestMatch = searchResults.maxByOrNull { it.accuracy ?: 0.0 }
            bestMatch?.let { displayBestMatch(it) }

            // Display other matches
            val otherMatches = searchResults.filter { it != bestMatch }
            displayOtherMatches(otherMatches)
        }

        // Set up generate report button
        findViewById<android.widget.Button>(R.id.generate_report).setOnClickListener {
            generateReport(searchResults)
        }
    }

    private fun displayBestMatch(result: SearchResult) {
        findViewById<TextView>(R.id.bestMatchName).text = result.name ?: "Unknown"
        findViewById<TextView>(R.id.bestMatchAccuracy).text = 
            String.format("Accuracy: %.1f%%", (result.accuracy ?: 0.0) * 100)
    }

    private fun displayOtherMatches(results: List<SearchResult>) {
        val container = findViewById<LinearLayout>(R.id.otherMatchesContainer)
        container.removeAllViews() // Clear any existing views
        
        results.forEach { result ->
            // Create a CardView programmatically
            val card = CardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 32) // 32dp bottom margin
                }
                radius = 16f // 16dp corner radius
                elevation = 8f // 8dp elevation
                setContentPadding(32, 32, 32, 32) // 32dp padding
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
                setTextColor(resources.getColor(android.R.color.black))
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
                setTextColor(resources.getColor(android.R.color.darker_gray))
            }
            content.addView(accuracyView)

            // Add content to card and card to container
            card.addView(content)
            container.addView(card)
        }
    }

    private fun generateReport(results: List<SearchResult>) {
        // TODO: Implement report generation
        android.widget.Toast.makeText(
            this,
            "Report generation coming soon!",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }
} 