package com.example.gaim.ui.account

import android.os.Bundle
import android.util.Log
import android.widget.Button

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gaim.R
import com.example.gaim.ui.AbstractActivity
import com.example.gaim.ui.utility.CustomAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gaim.account.settings.AccountSettings

class AccountActivity : AbstractActivity() {
    private val usernameID = R.id.user_name
    private lateinit var accountSettings: AccountSettings
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.savedaccount)

        //setting the top value to a username
        val username = intent.getStringExtra(Login.USERNAME.value)
        val password = intent.getStringExtra(Login.PASSWORD.value)
        accountSettings = AccountSettings(this, username.toString(), password.toString())

        //setting the top value to a username
        val usernameID = findViewById<TextView>(usernameID)
        usernameID.text = username

        //setting up previous user data
        val previousResults =  accountSettings.getAnimals()

        for (result in previousResults){
            Log.d("TAG", result.toString())
        }

        //adding previous user data to custom adapter
        val customAdapter = CustomAdapter(previousResults.toTypedArray())

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_reports)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = customAdapter

        backButton = findViewById(R.id.backButton)
        // Set up back button
        backButton.setOnClickListener {
            finish()
        }
    }

}