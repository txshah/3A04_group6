package com.example.gaim.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.gaim.search.GaimActivity

//MAKE SURE ALL ACTIVITIES IMPLEMENT THIS
abstract class AbstractActivity: AppCompatActivity () {

    fun nextActivity(activity: GaimActivity){
        val intent = Intent(this, activity.activity)

        startActivity(intent)
    }

    fun nextActivity(activity: GaimActivity, previousIntent: Intent){
        val intent = Intent(this, activity.activity)

        intent.putExtras(previousIntent)
        startActivity(intent)
    }

}