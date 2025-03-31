package com.example.gaim.ui.search

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.gaim.R

class FreeformSearchActivity: AbstractSearchActivity () {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.search)

        val textInput = findViewById<EditText>(R.id.input_text)

        val submitFreeform = findViewById<Button>(R.id.submit_freeform)

        submitFreeform.setOnClickListener {
            textInput.text
        }
        TODO("Not yet implemented")
    }

}