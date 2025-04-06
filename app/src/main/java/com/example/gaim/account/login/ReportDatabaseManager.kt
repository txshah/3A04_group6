package com.example.gaim.account.login

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
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
        Log.d(TAG, "saveAnimalReport() called for username: $username, animal: $animalName")
        try {
            // Open the database
            val dbPath = context.getDatabasePath(DB_NAME).absolutePath
            Log.d(TAG, "Opening database at path: $dbPath")
            
            val db: SQLiteDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
            Log.d(TAG, "Database opened successfully in READWRITE mode")
            
            try {
                // Ensure the reports table exists
                createReportsTableIfNeeded(db)
                
                // Get the user ID
                val userId = getUserIdByUsername(db, username)
                Log.d(TAG, "Retrieved user ID: $userId for username: $username")
                
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
                Log.d(TAG, "ContentValues prepared: $values")
                
                // Insert the report
                val result = db.insert("reports", null, values)
                Log.d(TAG, "Insert result: $result (row ID, -1 means failure)")
                
                val success = result != -1L
                Log.d(TAG, "Report save ${if (success) "successful" else "failed"}")
                
                return success
            } finally {
                db.close()
                Log.d(TAG, "Database closed")
            }
        } catch (e: SQLiteException) {
            Log.e(TAG, "Error saving animal report", e)
            Log.e(TAG, "SQL Exception message: ${e.message}")
            return false
        }
    }
    
    /**
     * Creates the reports table if it doesn't already exist.
     */
    private fun createReportsTableIfNeeded(db: SQLiteDatabase) {
        Log.d(TAG, "createReportsTableIfNeeded() called")
        try {
            val createTableSql = """
                CREATE TABLE IF NOT EXISTS reports (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    animal_name TEXT NOT NULL,
                    created_at INTEGER NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users (id)
                )
            """
            Log.d(TAG, "Executing SQL: $createTableSql")
            
            db.execSQL(createTableSql)
            Log.d(TAG, "Reports table created or already exists")
            
            // Check if table exists by querying it
            val cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='reports'", null)
            val tableExists = cursor.count > 0
            cursor.close()
            Log.d(TAG, "Verified reports table exists: $tableExists")
        } catch (e: SQLiteException) {
            Log.e(TAG, "Error creating reports table", e)
            Log.e(TAG, "SQL Exception message: ${e.message}")
            throw e
        }
    }
    
    /**
     * Gets the user ID given a username.
     * 
     * @return The user ID or -1 if not found
     */
    private fun getUserIdByUsername(db: SQLiteDatabase, username: String): Int {
        Log.d(TAG, "getUserIdByUsername() called for username: $username")
        try {
            val query = "SELECT id FROM users WHERE username = ?"
            Log.d(TAG, "Executing query: $query with params: [$username]")
            
            val cursor = db.rawQuery(query, arrayOf(username))
            logCursorResults(cursor, "users")
            
            val userId = if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                Log.d(TAG, "Found user ID: $id for username: $username")
                id
            } else {
                Log.d(TAG, "No user found with username: $username")
                -1
            }
            
            cursor.close()
            Log.d(TAG, "Cursor closed")
            return userId
        } catch (e: SQLiteException) {
            Log.e(TAG, "Error getting user ID", e)
            Log.e(TAG, "SQL Exception message: ${e.message}")
            return -1
        }
    }
    
    /**
     * Gets all reports for a given user.
     * 
     * @return List of animal names or empty list if none found
     */
    fun getUserReports(username: String): List<String> {
        Log.d(TAG, "getUserReports() called for username: $username")
        val reports = mutableListOf<String>()
        
        try {
            val dbPath = context.getDatabasePath(DB_NAME).absolutePath
            Log.d(TAG, "Opening database at path: $dbPath")
            
            val db: SQLiteDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
            Log.d(TAG, "Database opened successfully in READONLY mode")
            
            try {
                // Get the user ID
                val userId = getUserIdByUsername(db, username)
                Log.d(TAG, "Retrieved user ID: $userId for username: $username")
                
                if (userId == -1) {
                    Log.e(TAG, "Could not find user with username: $username")
                    return reports
                }
                
                // Query for reports
                val query = "SELECT animal_name FROM reports WHERE user_id = ? ORDER BY created_at DESC"
                Log.d(TAG, "Executing query: $query with params: [$userId]")
                
                val cursor = db.rawQuery(query, arrayOf(userId.toString()))
                logCursorResults(cursor, "reports")
                
                // Add reports to the list
                Log.d(TAG, "Found ${cursor.count} reports for user: $username")
                
                while (cursor.moveToNext()) {
                    val animalName = cursor.getString(cursor.getColumnIndexOrThrow("animal_name"))
                    reports.add(animalName)
                    Log.d(TAG, "Added report for animal: $animalName")
                }
                
                cursor.close()
                Log.d(TAG, "Cursor closed")
            } finally {
                db.close()
                Log.d(TAG, "Database closed")
            }
        } catch (e: SQLiteException) {
            Log.e(TAG, "Error retrieving user reports", e)
            Log.e(TAG, "SQL Exception message: ${e.message}")
        }
        
        Log.d(TAG, "Returning ${reports.size} reports: $reports")
        return reports
    }
    
    /**
     * Logs the contents of a cursor for debugging purposes.
     */
    private fun logCursorResults(cursor: Cursor, tableName: String) {
        val rowCount = cursor.count
        Log.d(TAG, "Query returned $rowCount rows from $tableName table")
        
        if (rowCount == 0) {
            Log.d(TAG, "No results found in $tableName table")
            return
        }
        
        // Log column names
        val columnNames = cursor.columnNames
        Log.d(TAG, "Columns: ${columnNames.joinToString(", ")}")
        
        // Log a sample of the data (up to 5 rows)
        val maxRowsToLog = Math.min(5, rowCount)
        Log.d(TAG, "Showing first $maxRowsToLog rows of $rowCount total:")
        
        val cursorPosition = cursor.position
        cursor.moveToPosition(-1) // Reset position
        
        var rowNum = 0
        while (cursor.moveToNext() && rowNum < maxRowsToLog) {
            val rowData = StringBuilder("Row $rowNum: ")
            for (i in 0 until cursor.columnCount) {
                val columnName = cursor.getColumnName(i)
                val value = when (cursor.getType(i)) {
                    Cursor.FIELD_TYPE_NULL -> "NULL"
                    Cursor.FIELD_TYPE_INTEGER -> cursor.getLong(i).toString()
                    Cursor.FIELD_TYPE_FLOAT -> cursor.getDouble(i).toString()
                    Cursor.FIELD_TYPE_STRING -> "\"${cursor.getString(i)}\""
                    Cursor.FIELD_TYPE_BLOB -> "[BLOB]"
                    else -> "?"
                }
                rowData.append("$columnName=$value, ")
            }
            Log.d(TAG, rowData.toString())
            rowNum++
        }
        
        // Restore cursor position
        cursor.moveToPosition(cursorPosition)
    }
} 