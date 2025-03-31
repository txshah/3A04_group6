package com.example.gaim

import android.os.Bundle
import android.util.Log
import android.widget.Button

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class SearchUI : AppCompatActivity () {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.freeform_search)

        //assigning buttons to values
        try{
            val submitButton = findViewById<Button>(R.id.start_search)


            Log.d("BUTTON", "Search buttons made")

            submitButton.setOnClickListener {
                Log.d("submit input", "submit input") // Prints "Hello" to logcat
                println("Hello") // Prints "Hello" to the console (debugging)
            }
        }catch (e: Error){
            print(e)
        }

    }
}