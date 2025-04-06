package com.example.gaim.ui.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.gaim.BuildConfig
import com.example.gaim.GeminiService
import com.example.gaim.MainActivity
import com.example.gaim.R
import com.example.gaim.account.login.ReportDatabaseManager
import com.example.gaim.account.login.UserSession
import com.example.gaim.search.SearchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class AnimalReportActivity : AppCompatActivity() {
    private val TAG = "AnimalReportActivity"
    
    private lateinit var nameTextView: TextView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var backButton: Button
    private lateinit var saveReportButton: Button
    private lateinit var animalImageView: ImageView
    private lateinit var animalInfoContainer: LinearLayout
    
    // Section TextViews
    private lateinit var descriptionText: TextView
    private lateinit var characteristicsText: TextView
    private lateinit var huntingText: TextView
    private lateinit var habitatText: TextView
    
    // Animal name from intent
    private lateinit var animalName: String
    
    // Google Custom Search API constants
    private val SEARCH_API_URL = "https://www.googleapis.com/customsearch/v1"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate started")
        
        try {
            setContentView(R.layout.animal_report)
            
            // Initialize views
            nameTextView = findViewById(R.id.animalNameText)
            loadingIndicator = findViewById(R.id.loadingIndicator)
            backButton = findViewById(R.id.backButton)
            saveReportButton = findViewById(R.id.saveReportButton)
            animalImageView = findViewById(R.id.animalImageView)
            animalInfoContainer = findViewById(R.id.animalInfoContainer)
            
            // Initialize section TextViews
            descriptionText = findViewById(R.id.descriptionText)
            characteristicsText = findViewById(R.id.characteristicsText)
            huntingText = findViewById(R.id.huntingText)
            habitatText = findViewById(R.id.habitatText)
            
            // Get animal name from intent
            animalName = intent.getStringExtra("animal_name") ?: "Unknown Animal"
            
            // Set animal name
            nameTextView.text = animalName
            Log.d(TAG, "Displaying animal: $animalName")
            
            // Set up back button
            backButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
            }
            
            // Set up save report button
            setupSaveReportButton()
            
            // Fetch animal image and data
            fetchAnimalImage(animalName)
            fetchAnimalData(animalName)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
        }
    }
    
    private fun setupSaveReportButton() {
        // Check if user is logged in
        if (UserSession.isLoggedIn()) {
            saveReportButton.setOnClickListener {
                saveAnimalReport()
            }
        } else {
            // Disable button if not logged in
            saveReportButton.isEnabled = false
            saveReportButton.text = "Login to Save"
            
            // Show login required toast when clicked
            saveReportButton.setOnClickListener {
                Toast.makeText(this, "You must be logged in to save reports", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun saveAnimalReport() {
        val username = UserSession.getUser()
        if (username != null) {
            val reportManager = ReportDatabaseManager(this)
            val success = reportManager.saveAnimalReport(username, animalName)
            
            if (success) {
                Toast.makeText(this, "Report saved successfully", Toast.LENGTH_SHORT).show()
                // Disable button after successful save to prevent duplicate saves
                saveReportButton.isEnabled = false
                saveReportButton.text = "Report Saved"
            } else {
                Toast.makeText(this, "Failed to save report", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "You must be logged in to save reports", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun fetchAnimalImage(animalName: String) {
        Log.d(TAG, "Fetching image for: $animalName")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Build the search URL
                val searchQuery = "$animalName animal"
                val urlString = "$SEARCH_API_URL?key=${BuildConfig.GOOGLE_SEARCH_API_KEY}&cx=${BuildConfig.GOOGLE_SEARCH_ENGINE_ID}&q=${searchQuery.replace(" ", "+")}&searchType=image&num=1"
                
                Log.d(TAG, "Making request to: $urlString")
                
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                
                // Read the response
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(response)
                
                // Parse the image URL from the response
                val items = jsonResponse.getJSONArray("items")
                if (items.length() > 0) {
                    val imageUrl = items.getJSONObject(0).getString("link")
                    Log.d(TAG, "Found image URL: $imageUrl")
                    
                    withContext(Dispatchers.Main) {
                        // Load the image using Glide
                        loadImageWithGlide(imageUrl)
                    }
                } else {
                    Log.w(TAG, "No images found for $animalName")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AnimalReportActivity, "No image found for $animalName", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching image", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AnimalReportActivity, "Error loading image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun loadImageWithGlide(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .error(android.R.drawable.ic_dialog_alert)
            .into(animalImageView)
    }
    
    private fun fetchAnimalData(animalName: String) {
        Log.d(TAG, "Fetching data for: $animalName")
        // Show loading indicator
        loadingIndicator.visibility = View.VISIBLE
        animalInfoContainer.visibility = View.GONE
        
        val geminiService = GeminiService(this)
        val prompt = """
            Generate detailed information about $animalName in JSON format with the following structure:
            {
              "description": "A brief overview of the animal (2-3 sentences)",
              "physical_characteristics": "Details about the animal's appearance, size, weight, etc. (3-4 sentences)",
              "habitat": "Information about where the animal lives (2-3 sentences)",
              "hunting": "Information about if this animal is hunted, hunting seasons, regulations, or conservation status (2-3 sentences)"
            }
            
            Return ONLY valid JSON that can be parsed, with no additional text or explanation. Ensure all escape characters are properly handled.
        """.trimIndent()
        
        geminiService.testGemini(
            modelName = "gemini-1.5-flash",
            customPrompt = prompt,
            onResult = { response ->
                Log.d(TAG, "Received response from Gemini")
                try {
                    // Parse the JSON response
                    val jsonResponse = cleanAndParseJson(response)
                    
                    // Populate the sections
                    populateSections(jsonResponse)
                    
                    // Hide loading indicator
                    loadingIndicator.visibility = View.GONE
                    animalInfoContainer.visibility = View.VISIBLE
                } catch (e: JSONException) {
                    Log.e(TAG, "Error parsing JSON response: ${e.message}", e)
                    Log.e(TAG, "Raw response: $response")
                    
                    // Show error to user
                    Toast.makeText(
                        this,
                        "Error parsing animal data",
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    // Display raw response if JSON parsing fails
                    descriptionText.text = response
                    loadingIndicator.visibility = View.GONE
                    animalInfoContainer.visibility = View.VISIBLE
                }
            }
        )
    }
    
    private fun cleanAndParseJson(response: String): JSONObject {
        // Sometimes Gemini might return text before or after the JSON
        // This method attempts to extract and parse just the JSON part
        
        // Find the beginning of the JSON object
        val startIndex = response.indexOf("{")
        val endIndex = response.lastIndexOf("}")
        
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            val jsonString = response.substring(startIndex, endIndex + 1)
            Log.d(TAG, "Extracted JSON: $jsonString")
            return JSONObject(jsonString)
        }
        
        // If we can't find clear JSON delimiters, try parsing the whole thing
        return JSONObject(response)
    }
    
    private fun populateSections(jsonData: JSONObject) {
        try {
            // Get values from JSON with fallbacks for missing fields
            val description = jsonData.optString("description", "No description available")
            val characteristics = jsonData.optString("physical_characteristics", "No physical characteristics information available")
            val habitat = jsonData.optString("habitat", "No habitat information available")
            val hunting = jsonData.optString("hunting", "No hunting information available")
            
            // Set the text for each section
            descriptionText.text = description
            characteristicsText.text = characteristics
            habitatText.text = habitat
            huntingText.text = hunting
            
            Log.d(TAG, "Populated animal information sections")
        } catch (e: Exception) {
            Log.e(TAG, "Error populating sections: ${e.message}", e)
            Toast.makeText(
                this,
                "Error displaying animal information",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
} 