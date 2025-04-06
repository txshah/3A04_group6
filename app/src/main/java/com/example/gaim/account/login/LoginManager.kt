package com.example.gaim.account.login
import android.content.Context
import com.example.gaim.account.login.AccountDatabaseManager

//manages entire login process and coordinates the UI before redirecting to homepage once login is complete
class LoginManager(private val context: Context) {

    fun verifyCredentials(username: String, password: String): Boolean {
        val dbManager = AccountDatabaseManager(context)
        return dbManager.validateLogin(username, password)
    }

}