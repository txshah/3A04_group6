package com.example.gaim.account.settings

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log
import com.example.gaim.search.SearchResult
import org.json.JSONArray
import org.json.JSONObject

class AccountSettings(private val context: Context, private val username: String, private val password: String) {
    private val dbPath = context.getDatabasePath("user_accounts.db").absolutePath

    // Save a simple animal entry
    fun save(animal: String, accuracy: Double?) {
        val jsonArray = getUserJsonData()

        val entry = JSONObject().apply {
            put("animal", animal)
            put("accuracy", accuracy) // Safe across all Android versions
            put("timestamp", System.currentTimeMillis())
        }

        jsonArray.put(entry)
        updateUserJsonData(jsonArray)
        Log.d("AccountSettings", "Saved animal: $animal, accuracy: $accuracy")
    }

    // Save from a SearchResult object
    fun save(searchResult: SearchResult) {
        val name = searchResult.name ?: "Unknown"
        val accuracy = searchResult.accuracy
        save(name, accuracy)
    }

    // Fetch all stored animal reports
    fun getAnimals(): MutableCollection<SearchResult> {
        val results = mutableListOf<SearchResult>()
        val jsonArray = getUserJsonData()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val name = obj.optString("animal", "Unknown")
            val accuracy = obj.optDouble("accuracy", 0.0)
            results.add(SearchResult(name = name, accuracy = accuracy))
        }

        Log.d("AccountSettings", "Loaded ${results.size} saved animals")
        return results
    }



    // Get the user's JSON history from the DB
    private fun getUserJsonData(): JSONArray {
        return try {
            val db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
            val cursor = db.rawQuery(
                "SELECT * FROM users WHERE username = ? AND password = ?",
                arrayOf(username, password)
            )

            var jsonArray = JSONArray()

            if (cursor.moveToFirst()) {
                val jsonString = cursor.getString(cursor.getColumnIndexOrThrow("reports_json"))
                if (!jsonString.isNullOrBlank()) {
                    jsonArray = JSONArray(jsonString)
                }
            }

            cursor.close()
            db.close()
            jsonArray
        } catch (e: Exception) {
            e.printStackTrace()
            JSONArray()
        }
    }

    // Update the user's history column
    private fun updateUserJsonData(updatedData: JSONArray) {
        try {
            val db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
            val values = ContentValues().apply {
                put("reports_json", updatedData.toString())
            }
            db.update("users", values, "username = ? AND password = ?", arrayOf(username, password))
            db.close()
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }
}
