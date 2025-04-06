package com.example.gaim.search.algorithm

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.gaim.search.SearchResult
import java.io.File

//see SEARCH ALGOIRTHM for description
class SurveySearchAlgorithm(private val context: Context) : SearchAlgorithm<String> {
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
        val variables = extract(input)
        val options = query(variables)

        if (options.isEmpty()) {
            return SearchResult("N/A", 0.0)
        }

        val maxEntry = options.maxBy { it.value }
        val species = maxEntry.key
        val accuracy = maxEntry.value.toDouble() / options.size / 100

        return SearchResult(species, accuracy)
    }

    private fun extract(input: String): HashMap<String, String> {
        val hashMap = HashMap<String, String>()
        input.split(";")
            .map { it.trim() }
            .forEach { pair ->
                val (key, value) = pair.split(":", limit = 2).map { it.trim() }
                hashMap[key] = value
            }

        return hashMap
    }

    private fun query(input: HashMap<String, String>): Map<String, Int> {
        val results = mutableMapOf<String, Int>()
        
        try {
            val legs = input["legs"]?.toIntOrNull()
            val coat = input["coat"]
            val colour = input["colour"]
            val domain = input["domain"]
            val region = input["region"]
            val size = input["size"]

            val selectionArgs = mutableListOf<String>()
            val selectionCriteria = mutableListOf<String>()

            legs?.let { 
                selectionCriteria.add("legs = ?")
                selectionArgs.add(it.toString())
            }
            coat?.let {
                selectionCriteria.add("coat = ?")
                selectionArgs.add(it)
            }
            colour?.let {
                selectionCriteria.add("colour = ?")
                selectionArgs.add(it)
            }
            domain?.let {
                selectionCriteria.add("domain = ?")
                selectionArgs.add(it)
            }
            region?.let {
                selectionCriteria.add("region = ?")
                selectionArgs.add(it)
            }
            size?.let {
                selectionCriteria.add("size = ?")
                selectionArgs.add(it)
            }

            val selection = selectionCriteria.joinToString(" AND ")
            
            val cursor = database?.query(
                "species",  // table name
                arrayOf("name"),  // columns to return
                selection,  // selection criteria
                selectionArgs.toTypedArray(),  // selection args
                null,  // group by
                null,  // having
                null   // order by
            )

            cursor?.use {
                while (it.moveToNext()) {
                    val name = it.getString(0)
                    results[name] = 100
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
