package com.example.gaim.account.login
import android.content.Context
import com.example.gaim.account.login.AccountDatabaseManager

//manages entire login process and coordinates the UI before redirecting to homepage once login is complete
class LoginManager(private val context: Context) {
    private val dbManager = AccountDatabaseManager(context)

    //makes sure the entry actually exists in the database
    fun verifyCredentials(username: String, password: String): Boolean {
        return dbManager.validateLogin(username, password)
    }

    //creates an account
    fun createAccount(username: String, password: String): Boolean {
        return dbManager.register(username, password)
    }

}