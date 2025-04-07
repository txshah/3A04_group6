package com.example.gaim.search.algorithm

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.gaim.search.SearchResult
import java.io.File

//see SEARCH ALGORITHM for description
class FreeformSearchAlgorithm(private val context: Context) : SearchAlgorithm<String> {
//    global vars - later
//    private val client = OkHttpClient()
//    private val apiToken = ""
//    private val modelUrl = ""
//DB SET UP
    private val DB_NAME = "canadian_species.db"
    private var dbFile: File? = null
    private var database: SQLiteDatabase? = null

    init {
        setupDatabase()
    }

    private fun setupDatabase() {
        // Get the database file in the app's private directory
        dbFile = context.getDatabasePath(DB_NAME)
        
        // If database doesn't exist in app's private directory, copy it from assets
        if (!dbFile!!.exists()) {
            dbFile!!.parentFile?.mkdirs()  // Create directories if they don't exist
            
            try {
                // Copy database from assets
                context.assets.open(DB_NAME).use { input ->
                    dbFile!!.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                throw RuntimeException("Error copying database from assets", e)
            }
        }

        // Open the database
        database = SQLiteDatabase.openDatabase(
            dbFile!!.absolutePath,
            null,
            SQLiteDatabase.OPEN_READONLY
        )
    }
//SEARCH FUNCTION
    override suspend fun search(input: String): SearchResult {
        // Extract up to 10 keywords from the input - max limit
        val keyWords = extract(input)
//from keywords query them and get hasmap
        val options = query(keyWords)

        if (options.isEmpty()) {
            return SearchResult("Unknown", 0.0)
        }

        val maxEntry = options.maxBy { it.value }
        val species = maxEntry.key
//    hashmap score out of matched keywords / total keywords
        val accuracy = maxEntry.value.toDouble() / keyWords.size

        return SearchResult(species, accuracy)
    }

    private fun extract(input: String): List<String>{
//        exactly 10 keywords from any input text
//        in one line split input into words and then get all words great than three and take 10 distinct
        var output = input.split(" ")
//            make sure sig word and chars
            .filter { it.length > 3 && it.matches(Regex("[a-zA-Z]+")) }
            .map { it.lowercase() }
            .distinct()
            .take(10)

        print("$output")

        return output
    }

    private fun query(input: List<String>): Map<String, Int> {
        val results = mutableMapOf<String, Int>()
        
        try {
            // Split input into words
            
            // Query each word
            for (word in input) {
//                query from the keywords col - different then query
                val cursor = database?.rawQuery(
                    "SELECT name FROM species WHERE keywords LIKE ?",
                    arrayOf("%$word%")
                )
//go through results from query and add to results (number of words matched to found word)
//                result from DB: number of keywords
                cursor?.use {
                    while (it.moveToNext()) {
                        val name = it.getString(0)
                        results[name] = results.getOrDefault(name, 0) + 1
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return results
    }

    fun cleanup() {
        database?.close()
        database = null
    }
}
