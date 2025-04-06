package com.example.gaim.ui.account

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.gaim.R
import com.example.gaim.account.login.LoginManager
import com.example.gaim.ui.AbstractActivity
import com.example.gaim.ui.MainpageActivity
import com.example.gaim.ui.utility.ErrorChecker
import com.example.gaim.ui.utility.MismatchText
import com.example.gaim.ui.utility.MissingText

class CreateAccountActivity : AbstractActivity() {
    private val TAG = "CreateAccountActivity"

    private val usernameID = R.id.username
    private val passwordID = R.id.password
    private val retypePasswordID = R.id.retype_password

    private val createAccountID = R.id.create_account

    private val loginManager = LoginManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            copyDatabaseFromAssetsIfNeeded(this, "user_accounts.db")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize database", e)
            Toast.makeText(this, "Error: Unable to access database", Toast.LENGTH_LONG).show()
            finish() // Close the activity since we can't proceed without the database
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_create_account)

        val usernameInput = findViewById<EditText>(usernameID)
        val passwordInput = findViewById<EditText>(passwordID)
        val retypePasswordInput = findViewById<EditText>(retypePasswordID)

        val createAccountButton = findViewById<Button>(createAccountID)

        val createAccountCheckers = mutableSetOf<ErrorChecker>(
            MissingText(this, usernameInput, "Username"),
            MissingText(this, passwordInput, "Password"),
            MissingText(this, retypePasswordInput, "Retype password"),
            MismatchText(this, passwordInput, retypePasswordInput)
        )

        createAccountButton.setOnClickListener {
            if(this.checkErrors(createAccountCheckers)){
                if(this.createAccount(usernameInput.text.toString(), passwordInput.text.toString())){
                    this.nextActivity(MainpageActivity.MAIN)
                }
            }

        }
    }

    private fun createAccount(username: String, password: String): Boolean{
        try{
            return loginManager.createAccount(username, password)
        }catch(e: Error){
            Log.d(TAG, e.toString())
            return false
        }

    }
}