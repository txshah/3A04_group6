package com.example.gaim.ui.account

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.gaim.R
import com.example.gaim.ui.AbstractActivity
import org.w3c.dom.Text

class AccountActivity : AbstractActivity() {
    private val usernameID = R.id.user_name
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.savedaccount)

        val username = intent.getStringExtra(Login.USERNAME.value)

        val usernameID = findViewById<TextView>(usernameID)

        usernameID.text = username

    }

}