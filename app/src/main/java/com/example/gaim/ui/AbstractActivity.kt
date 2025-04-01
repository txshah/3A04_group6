package com.example.gaim.ui

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.gaim.search.GaimActivity
import com.example.gaim.ui.utility.ErrorChecker

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

    fun showErrorDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss() // Closes the dialog
            }
            .show()
    }

    protected fun checkErrors(errorCheckers: MutableCollection<ErrorChecker>): Boolean{
        for (l in errorCheckers){
            if(!l.check()){
                return false
            }
        }

        return true
    }

}