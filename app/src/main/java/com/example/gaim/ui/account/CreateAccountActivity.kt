package com.example.gaim.ui.account

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import com.example.gaim.R
import com.example.gaim.ui.AbstractActivity
import com.example.gaim.ui.MainpageActivity
import com.example.gaim.ui.utility.ErrorChecker
import com.example.gaim.ui.utility.MissingText

class CreateAccountActivity : AbstractActivity() {
    private val usernameID = R.id.username
    private val passwordID = R.id.password
    private val retypePasswordID = R.id.retype_password

    private val createAccountID = R.id.create_account

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_account)

        val usernameInput = findViewById<EditText>(usernameID)
        val passwordInput = findViewById<EditText>(passwordID)
        val retypePasswordInput = findViewById<EditText>(retypePasswordID)

        val createAccountButton = findViewById<Button>(createAccountID)

        val createAccountCheckers = mutableSetOf<ErrorChecker>(
            MissingText(this, usernameInput, "Username"),
            MissingText(this, passwordInput, "Password"),
            MissingText(this, retypePasswordInput, "Retype password")
        )

        createAccountButton.setOnClickListener {
            if(this.checkErrors(createAccountCheckers)){
                if(this.createAccount()){
                    this.nextActivity(MainpageActivity.MAIN)
                }

            }

        }
    }

    private fun createAccount(): Boolean{
        //ADD IN ACCOUNT CREATION
        return true
    }
}