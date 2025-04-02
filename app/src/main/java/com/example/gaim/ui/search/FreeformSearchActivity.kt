package com.example.gaim.ui.search

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.gaim.R
import com.example.gaim.search.algorithm.FreeformSearchAlgorithm
import com.example.gaim.search.algorithm.SearchAlgorithm
import com.example.gaim.ui.utility.ErrorChecker
import com.example.gaim.ui.utility.MissingText

class FreeformSearchActivity : AbstractSearchActivity <String>() {
    override val algorithm: SearchAlgorithm<String> = FreeformSearchAlgorithm();

    private val inputID = R.id.input_text;
    private val submitID = R.id.submit_freeform;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.freeform_search)

        val textInput = findViewById<EditText>(inputID)

        val submitFreeform = findViewById<Button>(submitID)

        val submitFreeformErrorCheckers = mutableSetOf<ErrorChecker>(
            MissingText(this, textInput, "Your input")
        )

        submitFreeform.setOnClickListener {
            var currentText = textInput.text.toString()

            if(this.checkErrors(submitFreeformErrorCheckers)){
                completeSearch(currentText, this)
            }
        }

    }

}