package com.example.gaim

import android.os.Bundle
import android.util.Log
import android.widget.Button

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

//Homepage
class MainActivity : AppCompatActivity () {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        //assigning buttons to values
        try{
            Log.d("BUTTON", "Finding buttons");
            val homeButton = findViewById<Button>(R.id.home_page)
            val accountButton = findViewById<Button>(R.id.account_page)
            Log.d("BUTTON", "Topbar buttons made");

            //search buttons
            val surveySearchButton = findViewById<Button>(R.id.survey_search)
            val freeformSearchButton = findViewById<Button>(R.id.freeform_search)
            val imageSearchButton = findViewById<Button>(R.id.image_search)

            //start button
            val startSearchButton = findViewById<Button>(R.id.start_search)
            Log.d("BUTTON", "Search buttons made");

            homeButton.setOnClickListener {
                Log.d("BUTTON", "Hello") // Prints "Hello" to logcat
                println("Hello") // Prints "Hello" to the console (debugging)
            }
        }catch (e: Error){
            print(e)
        }

    }
}
