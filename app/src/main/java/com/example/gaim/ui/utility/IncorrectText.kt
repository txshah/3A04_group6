package com.example.gaim.ui.utility

import android.widget.EditText
import com.example.gaim.account.login.LoginManager
import com.example.gaim.ui.AbstractActivity

class IncorrectLogin (activity: AbstractActivity, private val username: EditText, private val password: EditText, private val loginManager: LoginManager) : TextError (activity, username) {
    override val errorTitle: String = "Incorrect field"
    override val errorMessage: String = "Either the username or password is incorrect! Please try again"

    override fun textCorrect(currentText: String): Boolean{
        return return loginManager.verifyCredentials(username.text.toString(), password.text.toString());
    }
}