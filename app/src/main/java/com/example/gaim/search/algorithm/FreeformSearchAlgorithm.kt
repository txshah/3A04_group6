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

    override fun search(input: String): SearchResult {
        val options = query(input)

        if (options.isEmpty()) {
            return SearchResult("N/A", 0.0)
        }

        val maxEntry = options.maxBy { it.value }
        val species = maxEntry.key
        val accuracy = maxEntry.value.toDouble() / options.size

        return SearchResult(species, accuracy)
    }

    private fun query(input: String): Map<String, Int> {
        val results = mutableMapOf<String, Int>()
        
        try {
            // Split input into words
            val words = input.split(" ")
            
            // Query each word
            for (word in words) {
                val cursor = database?.rawQuery(
                    "SELECT name FROM species WHERE " +
                    "name LIKE ? OR " +
                    "coat LIKE ? OR " +
                    "colour LIKE ? OR " +
                    "domain LIKE ? OR " +
                    "region LIKE ? OR " +
                    "size LIKE ?",
                    Array(6) { "%$word%" }
                )

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
