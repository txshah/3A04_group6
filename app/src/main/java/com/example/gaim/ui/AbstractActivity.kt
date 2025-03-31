package com.example.gaim.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

//MAKE SURE ALL ACTIVITIES IMPLEMENT THIS
abstract class AbstractActivity: AppCompatActivity () {

    fun nextActivity(activity: Class<out AppCompatActivity>){
        val intent = Intent(this, activity);

        startActivity(intent)
    }

    fun nextActivity(activity: Class<out AppCompatActivity>, previousIntent: Intent){
        val intent = Intent(this, activity);

        intent.putExtras(previousIntent)
        startActivity(intent)
    }
}