package com.example.gaim.ui.utility

import android.widget.EditText
import com.example.gaim.ui.AbstractActivity

class MismatchText (activity: AbstractActivity, private val password: EditText,  private val retyped: EditText) : TextError (activity, password) {
    override val errorTitle: String = "Password's don't match"
    override val errorMessage: String = "Your password doesn't match its retyping! Please try again"

    override fun textCorrect(currentText: String): Boolean{
        return password != retyped;
    }
}