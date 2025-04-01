package com.example.gaim.ui.utility

import android.widget.EditText
import com.example.gaim.ui.AbstractActivity

abstract class TextError (private val activity: AbstractActivity, private val textInput: EditText): ErrorChecker  {
    protected abstract val errorTitle: String
    protected abstract val errorMessage: String

    override fun check(): Boolean{
        var currentText = textInput.text.toString()
        var textCorrect = textCorrect(currentText);

        if(!textCorrect){
            activity.showErrorDialog(errorTitle, errorMessage)
        }

        return textCorrect
    }

    protected abstract fun textCorrect(currentText: String): Boolean
}