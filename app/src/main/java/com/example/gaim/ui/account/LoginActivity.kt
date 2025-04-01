package com.example.gaim.ui.account

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import com.example.gaim.R
import com.example.gaim.ui.*
import com.example.gaim.ui.utility.ErrorChecker
import com.example.gaim.ui.utility.MissingText

class LoginActivity : AbstractActivity()  {
    private val usernameID = R.id.username
    private val passwordID = R.id.password

    private val loginID = R.id.login
    private val createAccountID = R.id.create_account

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val usernameInput = findViewById<EditText>(usernameID)
        val passwordInput = findViewById<EditText>(passwordID)

        val loginButton = findViewById<Button>(loginID)
        val createAccountButton = findViewById<Button>(createAccountID)

        val loginCheckers = mutableSetOf<ErrorChecker>(
            MissingText(this, usernameInput, "Username"),
            MissingText(this, passwordInput, "Password")
        )

        loginButton.setOnClickListener {
            if(this.checkErrors(loginCheckers)){
                if(this.loginVerified()){
                    this.nextActivity(MainpageActivity.MAIN)
                }
            }
        }

        createAccountButton.setOnClickListener {
            this.nextActivity(MainpageActivity.CREATEACCOUNT)
        }
    }

    private fun loginVerified(): Boolean{
        //ADD IN LOGIN VERIFICATION
        return true
    }

}