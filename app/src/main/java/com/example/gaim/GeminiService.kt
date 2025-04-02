package com.example.gaim

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.ResponseStoppedException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

class GeminiService(private val context: Context) {
    
    private val TAG = "GeminiService"
    private val apiKey: String = BuildConfig.GEMINI_API_KEY
    private val modelName = "gemini-1.5-flash" // Using the auto-updated version
    
    fun testGemini(onResult: (String) -> Unit) {
        if (apiKey == "your_gemini_api_key_here") {
            val message = "Please update the GEMINI_API_KEY field in app/build.gradle.kts with your Gemini API key"
            Log.e(TAG, message)
            onResult("API key not set")
            return
        }
        
        // Check for internet connectivity before making the API call
        if (!isNetworkAvailable()) {
            Log.e(TAG, "No internet connection available")
            onResult("No internet connection")
            return
        }
        
        Log.d(TAG, "Starting Gemini API call with model: $modelName")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Configure the model using the Builder pattern
                val configBuilder = GenerationConfig.Builder()
                configBuilder.temperature = 0.7f
                configBuilder.topK = 1
                configBuilder.topP = 0.95f
                configBuilder.maxOutputTokens = 1000
                val config = configBuilder.build()
                
                Log.d(TAG, "Configuration created successfully")
                
                // Define safety settings to avoid blocks
                val safetySettings = listOf(
                    SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE),
                    SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE),
                    SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE),
                    SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE)
                )
                
                Log.d(TAG, "Safety settings created successfully")
                
                // Create the GenerativeModel with the API key
                Log.d(TAG, "Creating GenerativeModel...")
                val model = GenerativeModel(
                    modelName = modelName,
                    apiKey = apiKey,
                    generationConfig = config,
                    safetySettings = safetySettings
                )
                
                // Create a simple text prompt
                val prompt = "Hello! This is a test message from my Android app. Please respond with a brief greeting."
                Log.d(TAG, "Sending prompt to Gemini: $prompt")
                
                // Generate content
                val response = model.generateContent(prompt)
                Log.d(TAG, "Response received from Gemini")
                
                // Get the text from the response
                val responseText = response.text
                
                if (responseText == null) {
                    val candidates = response.candidates?.size ?: 0
                    val finishReason = response.candidates?.firstOrNull()?.finishReason ?: "unknown"
                    val detailedInfo = "Response received but text is null. Candidates: $candidates, Finish reason: $finishReason"
                    Log.w(TAG, detailedInfo)
                    
                    // Print full response for debugging
                    Log.w(TAG, "Full response object: $response")
                    if (response.candidates != null) {
                        for ((index, candidate) in response.candidates!!.withIndex()) {
                            Log.w(TAG, "Candidate $index: $candidate")
                            Log.w(TAG, "Candidate $index content: ${candidate.content}")
                        }
                    }
                    
                    withContext(Dispatchers.Main) {
                        onResult("No response text received")
                    }
                } else {
                    Log.d(TAG, "Response text: $responseText")
                    
                    withContext(Dispatchers.Main) {
                        onResult(responseText)
                    }
                }
            } catch (e: UnknownHostException) {
                // Specific handling for network connectivity issues
                Log.e(TAG, "Network connectivity issue: Unable to resolve host", e)
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Network error: Cannot connect to Gemini API", Toast.LENGTH_SHORT).show()
                    onResult("Network error: Check your internet connection")
                }
            } catch (e: ResponseStoppedException) {
                val stopReason = e.message ?: "Unknown"
                val errorMessage = "Response stopped. Reason: $stopReason"
                Log.e(TAG, errorMessage, e)
                
                // Print detailed stack trace
                Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Response stopped by safety filters", Toast.LENGTH_SHORT).show()
                    onResult("Response was blocked")
                }
            } catch (e: Exception) {
                // Check if the underlying cause is a network issue
                val rootCause = findRootCause(e)
                if (rootCause is UnknownHostException) {
                    Log.e(TAG, "Network connectivity issue (nested): ${rootCause.message}", e)
                    
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Network error: Cannot connect to Gemini API", Toast.LENGTH_SHORT).show()
                        onResult("Network error: Check your internet connection")
                    }
                } else {
                    val errorDetail = "Error class: ${e.javaClass.simpleName}, Message: ${e.message}"
                    Log.e(TAG, "Failed to get response from Gemini: $errorDetail", e)
                    
                    // Print detailed stack trace
                    Log.e(TAG, "Full error details: ${e.stackTraceToString()}")
                    
                    // Print API key length for debugging (don't show the actual key)
                    Log.d(TAG, "API key length: ${apiKey.length}")
                    
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error communicating with Gemini", Toast.LENGTH_SHORT).show()
                        onResult("Error: Failed to get response")
                    }
                }
            }
        }
    }
    
    // Helper method to check for internet connectivity
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    // Helper method to find the root cause of an exception
    private fun findRootCause(throwable: Throwable): Throwable {
        var rootCause: Throwable = throwable
        while (rootCause.cause != null && rootCause.cause != rootCause) {
            rootCause = rootCause.cause!!
        }
        return rootCause
    }
} 