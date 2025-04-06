package com.example.gaim.ui.account

import android.os.Bundle
import com.example.gaim.R
import com.example.gaim.ui.AbstractActivity

class AccountActivity : AbstractActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.savedaccount)

        val username = intent.getStringExtra(Login.USERNAME.value)
        val password = intent.getStringExtra(Login.PASSWORD.value)

    }

}