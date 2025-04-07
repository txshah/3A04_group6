package com.example.gaim.ui

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.gaim.account.settings.AccountSettings
import com.example.gaim.search.GaimActivity
import com.example.gaim.search.SearchActivity
import com.example.gaim.search.SearchResult
import com.example.gaim.ui.account.Login
import com.example.gaim.ui.search.AnimalReportActivity
import com.example.gaim.ui.utility.ErrorChecker

//MAKE SURE ALL ACTIVITIES IMPLEMENT THIS
abstract class AbstractActivity: AppCompatActivity () {

    open fun nextActivity(activity: GaimActivity, previousIntent: Intent){
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

    fun saveResult(result: SearchResult){
        val username = intent.getStringExtra(Login.USERNAME.value).toString()
        val password = intent.getStringExtra(Login.PASSWORD.value).toString()

        val accountSettings = AccountSettings(this, username, password)

        accountSettings.save(result)
    }

}