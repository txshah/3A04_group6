package com.example.gaim.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

abstract class AbstractActivity: AppCompatActivity () {

    public fun nextActivity(activity: Class<out AppCompatActivity>){
        val intent = Intent(this, activity);

        startActivity(intent)
    }

    public fun nextActivity(activity: Class<out AppCompatActivity>, previousIntent: Intent){
        val intent = Intent(this, activity);

        intent.putExtras(previousIntent)
        startActivity(intent)
    }
}