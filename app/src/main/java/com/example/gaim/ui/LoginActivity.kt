package com.example.gaim.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.gaim.MainActivity
import com.example.gaim.R
import com.example.gaim.ui.utility.ErrorChecker
import com.example.gaim.ui.utility.MissingText

class LoginActivity : AbstractActivity ()  {
    private val usernameID = R.id.username
    private val passwordID = R.id.password
    private val loginID = R.id.login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val usernameInput = findViewById<EditText>(usernameID)
        val passwordInput = findViewById<EditText>(passwordID)
        val loginButton = findViewById<Button>(loginID)

        val loginCheckers = mutableSetOf<ErrorChecker>(
            MissingText(this, usernameInput, "Username"),
            MissingText(this, passwordInput, "Password")
        )

        //sample button listener
        loginButton.setOnClickListener {
            if(this.checkErrors(loginCheckers)){
                complete()
            }
        }
    }

    private fun complete(){
        val intent = Intent(this, MainActivity::class.java);

        intent.putExtra("login", true)
        startActivity(intent)
    }
}