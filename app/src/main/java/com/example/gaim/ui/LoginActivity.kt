package com.example.gaim.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.gaim.MainActivity
import com.example.gaim.R

class LoginActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.login)



        //sample button listener
        loginButton.setOnClickListener {
            complete()
        }
    }

    private fun complete(){
        val intent = Intent(this, MainActivity::class.java);

        intent.putExtra("login", true)
        startActivity(intent)
    }
}