package com.example.gaim

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.gaim.R
import com.example.gaim.ui.AbstractActivity

class AccountActivity : AbstractActivity () {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_account)
    }
}