package com.example.gaim.ui.utility

import android.widget.Button
import android.widget.EditText
import com.example.gaim.ui.AbstractActivity

//checks if empty text given an activity, input field, and button field
class MissingText (activity: AbstractActivity, private val textInput: EditText) : TextError (activity, textInput) {
    override val errorTitle: String = "Missing text"
    override val errorMessage: String = "An input field is missing text!"

    private val emptyText: String = this.textInput.text.toString()

    override fun textCorrect(currentText: String): Boolean{
        return currentText != emptyText;
    }
}