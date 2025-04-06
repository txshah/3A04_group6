package com.example.gaim.ui.search

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import com.example.gaim.R
import com.example.gaim.search.algorithm.FreeformSearchAlgorithm
import com.example.gaim.search.algorithm.SearchAlgorithm
import com.example.gaim.ui.search.SurveySearchActivity
import com.example.gaim.ui.utility.ErrorChecker
import com.example.gaim.ui.utility.MissingText
import kotlinx.coroutines.launch

class FreeformSearchActivity : AbstractSearchActivity<String>() {
    override val algorithm: SearchAlgorithm<String> by lazy {
        FreeformSearchAlgorithm(this)
    }

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
            val currentText = textInput.text.toString()

            if (checkErrors(submitFreeformErrorCheckers)) {
                Log.d("FreeformSearchActivity", "Form validation passed, calling completeSearch")
                lifecycleScope.launch {
                    completeSearch(currentText, this@FreeformSearchActivity)
                }
//                completeSearch(currentText, this)
                Log.d("FreeformSearchActivity", "completeSearch completed")
            } else {
                Log.d("FreeformSearchActivity", "Form validation failed")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (algorithm as? FreeformSearchAlgorithm)?.cleanup()
    }
}