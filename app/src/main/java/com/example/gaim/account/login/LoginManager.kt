package com.example.gaim.account.login
import android.content.Context
import com.example.gaim.account.login.AccountDatabaseManager

//manages entire login process and coordinates the UI before redirecting to homepage once login is complete
class LoginManager(private val context: Context) {

    public fun verifyCredentials(username: String, password: String): Boolean {
        val dbManager = AccountDatabaseManager(context)
        return dbManager.validateLogin(username, password)
    }

    //displays login page with option to create account
    //If logging in: receives info from login page UI and authenticates account
    //if authentication succeeds, then displays success message and terminates (management of following screen is done by main or whatever)
    //if authentication fails, then displays error message and loops
    //create account option triggers send to account creator which would manage that page and register it with the database
    public fun run() {

        TODO("Not yet implemented")
    }
}