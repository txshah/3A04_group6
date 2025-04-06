package com.example.gaim.account.login

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log

class ReportDatabaseManager(private val context: Context) {
    private val TAG = "ReportDatabaseManager"
    private val DB_NAME = "user_accounts.db"
    
    /**
     * Saves an animal report to the database.
     * If the reports table doesn't exist, it will be created.
     * 
     * @param username The username of the current user
     * @param animalName The name of the animal in the report
     * @return true if save successful, false otherwise
     */
    fun saveAnimalReport(username: String, animalName: String): Boolean {
        try {
            // Open the database
            val dbPath = context.getDatabasePath(DB_NAME).absolutePath
            val db: SQLiteDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
            
            try {
                // Ensure the reports table exists
                createReportsTableIfNeeded(db)
                
                // Get the user ID
                val userId = getUserIdByUsername(db, username)
                if (userId == -1) {
                    Log.e(TAG, "Could not find user with username: $username")
                    return false
                }
                
                // Create the content values to insert
                val values = ContentValues().apply {
                    put("user_id", userId)
                    put("animal_name", animalName)
                    put("created_at", System.currentTimeMillis())
                }
                
                // Insert the report
                val result = db.insert("reports", null, values)
                
                return result != -1L
            } finally {
                db.close()
            }
        } catch (e: SQLiteException) {
            Log.e(TAG, "Error saving animal report", e)
            return false
        }
    }
    
    /**
     * Creates the reports table if it doesn't already exist.
     */
    private fun createReportsTableIfNeeded(db: SQLiteDatabase) {
        try {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS reports (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    animal_name TEXT NOT NULL,
                    created_at INTEGER NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users (id)
                )
            """)
            Log.d(TAG, "Reports table created or already exists")
        } catch (e: SQLiteException) {
            Log.e(TAG, "Error creating reports table", e)
            throw e
        }
    }
    
    /**
     * Gets the user ID given a username.
     * 
     * @return The user ID or -1 if not found
     */
    private fun getUserIdByUsername(db: SQLiteDatabase, username: String): Int {
        try {
            val cursor = db.rawQuery(
                "SELECT id FROM users WHERE username = ?",
                arrayOf(username)
            )
            
            val userId = if (cursor.moveToFirst()) {
                cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            } else {
                -1
            }
            
            cursor.close()
            return userId
        } catch (e: SQLiteException) {
            Log.e(TAG, "Error getting user ID", e)
            return -1
        }
    }
    
    /**
     * Gets all reports for a given user with full details.
     * 
     * @return List of SavedReport objects or empty list if none found
     */
    fun getUserReports(username: String): List<SavedReport> {
        val reports = mutableListOf<SavedReport>()
        
        try {
            val dbPath = context.getDatabasePath(DB_NAME).absolutePath
            val db: SQLiteDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
            
            try {
                // Get the user ID
                val userId = getUserIdByUsername(db, username)
                if (userId == -1) {
                    Log.e(TAG, "Could not find user with username: $username")
                    return reports
                }
                
                // Query for reports
                val cursor = db.rawQuery(
                    "SELECT id, animal_name, created_at FROM reports WHERE user_id = ? ORDER BY created_at DESC",
                    arrayOf(userId.toString())
                )
                
                // Add reports to the list
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val animalName = cursor.getString(cursor.getColumnIndexOrThrow("animal_name"))
                    val createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"))
                    
                    reports.add(SavedReport(id, animalName, createdAt))
                }
                
                cursor.close()
                Log.d(TAG, "Retrieved ${reports.size} reports for user $username")
            } finally {
                db.close()
            }
        } catch (e: SQLiteException) {
            Log.e(TAG, "Error retrieving user reports", e)
        }
        
        return reports
    }
    
    /**
     * Deletes a specific report by ID.
     * 
     * @return true if deletion successful, false otherwise
     */
    fun deleteReport(reportId: Int): Boolean {
        try {
            val dbPath = context.getDatabasePath(DB_NAME).absolutePath
            val db: SQLiteDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
            
            try {
                val deletedRows = db.delete("reports", "id = ?", arrayOf(reportId.toString()))
                return deletedRows > 0
            } finally {
                db.close()
            }
        } catch (e: SQLiteException) {
            Log.e(TAG, "Error deleting report", e)
            return false
        }
    }
} 