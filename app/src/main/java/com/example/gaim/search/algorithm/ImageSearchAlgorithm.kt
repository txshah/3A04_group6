package com.example.gaim.search.algorithm

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

// GOOGLE CLOUD VISION API KEY
private const val API_KEY = "AIzaSyAakFzZed_NUhPYQSOCFyxyAu9soZVTd_g"

class ImageSearchAlgorithm : SearchAlgorithm<String> {
    override fun search(input: String): SearchResult {
        println("Starting image search...")

        val filePath = if (input.isNotEmpty()) input
        else "app/src/main/java/com/example/gaim/account/database/redfox.jpg"

        println("Processing file: $filePath")

        val detectedLabels = apiCall(filePath)

        if (detectedLabels.isEmpty()) {
            println("No labels detected.")
            return SearchResult("N/A", 0.0)
        }

        val maxEntry = detectedLabels.maxByOrNull { it.value } ?: return SearchResult("N/A", 0.0)
        val species = maxEntry.key
        val accuracy = maxEntry.value.toDouble()

        println("Detected: $species with confidence: $accuracy")

        return SearchResult(species, accuracy)
    }


    private fun apiCall(filePath: String): Map<String, Float> {
        print("here")
        val labelsMap = mutableMapOf<String, Float>()
        try {
            val file = File(filePath)
            val imgBytes = Files.readAllBytes(file.toPath())
            val base64Image = Base64.getEncoder().encodeToString(imgBytes)

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

            val url = URL("https://vision.googleapis.com/v1/images:annotate?key=$API_KEY")
            print("Here")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            connection.outputStream.use { os ->
                os.write(jsonRequest.toByteArray())
                os.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode != 200) {
                val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() }
                println("Error: HTTP $responseCode - $errorResponse")
                return labelsMap
            }

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JsonParser.parseString(response).asJsonObject
            val labels = jsonResponse.getAsJsonArray("responses")
                .firstOrNull()?.asJsonObject
                ?.getAsJsonArray("labelAnnotations") ?: return labelsMap

            for (label in labels) {
                val obj = label as JsonObject
                labelsMap[obj.get("description").asString] = obj.get("score").asFloat
                print(labelsMap)
            }
        } catch (e: Exception) {
            println("Error processing image: ${e.message}")
        }
        return labelsMap
    }
}

fun main() {
    val searcher = ImageSearchAlgorithm()
    val result = searcher.search("app/src/main/java/com/example/gaim/account/database/redfox.jpg")
    print(result)
}
