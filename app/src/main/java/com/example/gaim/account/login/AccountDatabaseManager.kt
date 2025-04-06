package com.example.gaim.account.login
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log

//registers Accounts to the account database and fetches from the database, essentially a class to interface with the
//Account database
class AccountDatabaseManager(private val context: Context) {
    private val TAG = "AccountDatabaseManager"
    private val DB_NAME = "user_accounts.db"
    
    //returns true if login is valid, false if invalid
    fun validateLogin(username: String, password: String): Boolean {
        Log.d(TAG, "validateLogin() called for username: $username")
        try {
            val dbPath = context.getDatabasePath(DB_NAME).absolutePath
            Log.d(TAG, "Opening database at path: $dbPath")
            
            val db: SQLiteDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
            Log.d(TAG, "Database opened successfully in READONLY mode")

            val query = "SELECT * FROM users WHERE username = ? AND password = ?"
            Log.d(TAG, "Executing query: $query with params: [$username, ${password.replace(Regex("."), "*")}]")
            
            val cursor = db.rawQuery(query, arrayOf(username, password))
            logCursorResults(cursor, "users")

            val isValid = cursor.count > 0
            Log.d(TAG, "Login validation result: $isValid (found ${cursor.count} matching rows)")

            cursor.close()
            Log.d(TAG, "Cursor closed")
            
            db.close()
            Log.d(TAG, "Database closed")

            // If login is successful, set the current user in the session
            if (isValid) {
                UserSession.setUser(username)
                Log.d(TAG, "User session set for: $username")
            }

            return isValid
        } catch (e: SQLiteException) {
            Log.e(TAG, "Error validating login", e)
            Log.e(TAG, "SQL Exception message: ${e.message}")
            return false
        }
    }

    //returns true if registration successful, false if unsuccessful
    public fun register(username: String, password: String): Boolean{
        Log.d(TAG, "register() called for username: $username")
        // TODO: Implement registration logic
        Log.d(TAG, "register() not yet implemented")
        return false
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
                
                // Mask password values for security
                val value = if (columnName.equals("password", ignoreCase = true)) {
                    "\"********\""
                } else {
                    when (cursor.getType(i)) {
                        Cursor.FIELD_TYPE_NULL -> "NULL"
                        Cursor.FIELD_TYPE_INTEGER -> cursor.getLong(i).toString()
                        Cursor.FIELD_TYPE_FLOAT -> cursor.getDouble(i).toString()
                        Cursor.FIELD_TYPE_STRING -> "\"${cursor.getString(i)}\""
                        Cursor.FIELD_TYPE_BLOB -> "[BLOB]"
                        else -> "?"
                    }
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