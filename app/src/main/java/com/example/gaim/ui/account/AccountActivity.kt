package com.example.gaim.ui.account

import android.os.Bundle

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gaim.R
import com.example.gaim.ui.AbstractActivity
import com.example.gaim.ui.utility.CustomAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gaim.account.settings.AccountSettings
import com.example.gaim.ui.MainpageActivity

class AccountActivity : AbstractActivity() {
    private val usernameID = R.id.user_name
    private val homeID = R.id.homepage2
    private lateinit var accountSettings: AccountSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.savedaccount)

        //setting the top value to a username
        val username = intent.getStringExtra(Login.USERNAME.value)
        val password = intent.getStringExtra(Login.PASSWORD.value)
        accountSettings = AccountSettings(this, username.toString(), password.toString())

        setUsername(username.toString())
        setHome()
        showPreviousResults()
    }

    //sets topbar val to current user
    private fun setUsername(username: String){
        //setting the top value to a username
        val usernameID = findViewById<TextView>(usernameID)
        usernameID.text = username
    }

    //sets up the home button
    private fun setHome(){
        val homeID = findViewById<TextView>(homeID)

        homeID.setOnClickListener {
            nextActivity(MainpageActivity.MAIN, intent)
        }
    }

    //sets up previous results in recycler frame
    private fun showPreviousResults(){
        //setting up previous user data
        val previousResults =  accountSettings.getAnimals()

        //adding previous user data to custom adapter
        val customAdapter = CustomAdapter(previousResults.toTypedArray())

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_reports)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = customAdapter
    }
}