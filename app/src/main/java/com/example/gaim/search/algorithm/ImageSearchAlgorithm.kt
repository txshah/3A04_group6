package com.example.gaim.search.algorithm

import android.content.Context
import android.util.Log
import com.example.gaim.BuildConfig
import com.example.gaim.search.SearchResult
import java.io.File
import java.net.URISyntaxException
import java.net.URL
import java.net.HttpURLConnection
import java.nio.file.Files
import java.util.Base64
import com.google.gson.JsonParser
import com.google.gson.JsonObject
import com.google.protobuf.ByteString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
//        DEBUG REFERENCES
//  reference for DB set up :https://gist.github.com/wontondon/1271795
// reference for cursor use: https://cursa.app/en/page/working-with-sqlite-databases-working-with-cursors
//reference for cloud api: https://clouddevs.com/kotlin/google-cloud-vision/?
//image compression: https://gupta-kavya.medium.com/optimizing-image-upload-to-the-server-android-f27239785125
//API: https://medium.com/%40johnidouglasmarangon/google-cloud-vision-api-for-image-handling-and-ocr-a6763969a2e6

// GOOGLE CLOUD VISION API KEY
private const val API_KEY = "AIzaSyAakFzZed_NUhPYQSOCFyxyAu9soZVTd_g"
private const val TAG = "ImageSearchAlgorithm"

// Helper function to handle logging in both Android and standalone environments
private fun logMessage(level: String, tag: String, message: String, throwable: Throwable? = null) {
    try {
        // Try to use Android logging
        when (level) {
            "DEBUG" -> Log.d(tag, message, throwable)
            "INFO" -> Log.i(tag, message, throwable)
            "WARN" -> Log.w(tag, message, throwable)
            "ERROR" -> Log.e(tag, message, throwable)
            else -> Log.d(tag, message, throwable)
        }
    } catch (e: Exception) {
        // Fall back to standard output for standalone Java/Kotlin environment
        val prefix = "[$level][$tag] "
        println(prefix + message)
        throwable?.printStackTrace()
    }
}

class ImageSearchAlgorithm (private val context: Context) : SearchAlgorithm<String> {
    // Store last raw response and status code
    private var lastRawResponse: String? = null
    private var lastStatusCode: Int = 0

    // Expose last raw response
    fun getLastRawResponse(): String? = lastRawResponse

    // Expose last status code
    fun getLastStatusCode(): Int = lastStatusCode
//suspend search used to make sure api works
    override suspend fun search(input: String): SearchResult {
        logMessage("DEBUG", TAG, "Starting image search...")


        val filePath = if (input.isNotEmpty()) input
        else {
            val fallback = File(context.filesDir, "app/src/main/java/com/example/gaim/account/database/redfox.jpg").absolutePath
            fallback
            logMessage("DEBUG", TAG, "FallBack: ${fallback}")
        }
        logMessage("DEBUG", TAG, "newFile: ${filePath}")

        logMessage("DEBUG", TAG, "Processing file: $filePath")

        val detectedLabels = withContext(Dispatchers.IO) {
            apiCall(filePath.toString())
        }
        logMessage("DEBUG", TAG, "Detected labels: $detectedLabels")

        if (detectedLabels.isEmpty()) {
            logMessage("WARN", TAG, "No labels detected for the image")
            return SearchResult("Unknown", 0.0)
        }
//max entry chosen from many options returned by api
        val maxEntry = detectedLabels.maxByOrNull { it.value } ?: return SearchResult("N/A", 0.0)
        val species = maxEntry.key
        val accuracy = maxEntry.value.toDouble()

        logMessage("INFO", TAG, "Detected species: $species with confidence: $accuracy")

        return SearchResult(species, accuracy)
    }

//api call - use google vision key
    private fun apiCall(filePath: String): Map<String, Float> {
        logMessage("DEBUG", TAG, "Making API call to Google Vision API for file: $filePath")
        val labelsMap = mutableMapOf<String, Float>()
        try {
            val file = File(filePath)
            if (!file.exists()) {
                logMessage("ERROR", TAG, "File does not exist: $filePath")
                return labelsMap
            }
//make image smaller to make sure it fits
            logMessage("DEBUG", TAG, "Reading file bytes and encoding to Base64")
            val imgBytes = Files.readAllBytes(file.toPath())
            val base64Image = Base64.getEncoder().encodeToString(imgBytes)
            logMessage("DEBUG", TAG, "File size: ${imgBytes.size} bytes")

            val jsonRequest = """
                {
                  "requests": [
                    {
                      "image": { "content": "$base64Image" },
                      "features": [{ "type": "LABEL_DETECTION", "maxResults": 10 }]
                    }
                  ]
                }
            """.trimIndent()
//URL to based on API
            val url = URL("https://vision.googleapis.com/v1/images:annotate?key=$API_KEY")
            logMessage("DEBUG", TAG, "Sending request to: ${url.toString()}")

            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            logMessage("DEBUG", TAG, "Writing request data to connection")
            connection.outputStream.use { os ->
                os.write(jsonRequest.toByteArray())
                os.flush()
            }
//RESPONSE FROM API
            val responseCode = connection.responseCode
            this.lastStatusCode = responseCode
            logMessage("DEBUG", TAG, "Received response code: $responseCode")

            if (responseCode != 200) {
                val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() }
                this.lastRawResponse = errorResponse
                logMessage("ERROR", TAG, "Error from Vision API: HTTP $responseCode - $errorResponse")
                return labelsMap
            }

            logMessage("DEBUG", TAG, "Reading response from Vision API")
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            // Store the raw response
            this.lastRawResponse = response
            logMessage("DEBUG", TAG, "Raw API response: $response")
//annotiations are the rsponses
            logMessage("DEBUG", TAG, "Parsing JSON response")
            val jsonResponse = JsonParser.parseString(response).asJsonObject
            val annotations = jsonResponse.getAsJsonArray("responses")
                .firstOrNull()?.asJsonObject
                ?.getAsJsonArray("labelAnnotations")
//debug stuff
            if (annotations == null) {
                logMessage("WARN", TAG, "No label annotations found in response")
                return labelsMap
            }

            logMessage("DEBUG", TAG, "Processing ${annotations.size()} label annotations")
//            go through annoitations iteratively and get score and name in labelsMap
            for (label in annotations) {
                val obj = label as JsonObject
                val description = obj.get("description").asString
                val score = obj.get("score").asFloat
                labelsMap[description] = score
                logMessage("DEBUG", TAG, "Label: $description, Score: $score")
            }

            logMessage("INFO", TAG, "Successfully processed ${labelsMap.size} labels")
        } catch (e: Exception) {
            logMessage("ERROR", TAG, "Error processing image: ${e.message}", e)
        }
        return labelsMap
    }
}

//testing function
fun main() {
    println("==== Running ImageSearchAlgorithm ====")
//    val searcher = ImageSearchAlgorithm()
//    val result = searcher.search("app/src/main/java/com/example/gaim/account/database/redfox.jpg")
//    println("==== SEARCH RESULT ====")
//    println(result)
//    // Print the raw response
//    println("==== RAW API RESPONSE ====")
//    println(searcher.getLastRawResponse())
}
