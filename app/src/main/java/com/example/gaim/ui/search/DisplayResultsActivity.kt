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

        // Get search results from the SearchController
        val searchResults = intent.getSerializableExtra("search_results") as? List<SearchResult> ?: emptyList()

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
        findViewById<TextView>(R.id.tvBestMatchName).text = result.name ?: "Unknown"
        findViewById<TextView>(R.id.tvBestMatchAccuracy).text = 
            String.format("Accuracy: %.1f%%", (result.accuracy ?: 0.0) * 100)
    }

    private fun displayOtherMatches(results: List<SearchResult>) {
        val container = findViewById<LinearLayout>(R.id.otherMatchesContainer)
        
        results.forEach { result ->
            val card = LayoutInflater.from(this).inflate(
                R.layout.item_other_match,
                container,
                false
            ) as CardView

            card.findViewById<TextView>(R.id.tvAnimalName).text = result.name ?: "Unknown"
            card.findViewById<TextView>(R.id.tvAccuracy).text = 
                String.format("Accuracy: %.1f%%", (result.accuracy ?: 0.0) * 100)

            container.addView(card)
        }
    }

    private fun generateReport(results: List<SearchResult>) {
        // TODO: Implement report generation
        // This can be implemented based on your requirements
    }
} 