package com.example.gaim.account.login
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException

//registers Accounts to the account database and fetches from the database, essentially a class to interface with the
//Account database
class AccountDatabaseManager(private val context: Context) {
    //returns true if registration successful, false if unsuccessful

    fun validateLogin(username: String, password: String): Boolean {
        try {
            val dbPath = context.getDatabasePath("user_accounts.db").absolutePath
            val db: SQLiteDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)

            val cursor = db.rawQuery(
                "SELECT * FROM users WHERE username = ? AND password = ?",
                arrayOf(username, password)
            )

            val isValid = cursor.count > 0

            cursor.close()
            db.close()

            return isValid
        } catch (e: SQLiteException) {
            e.printStackTrace()
            return false
        }
    }

    public fun register(username: String, password: String): Boolean{
        TODO("Not yet implemented")
    }
}