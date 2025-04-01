package com.example.gaim.ui

import com.example.gaim.AccountActivity
import com.example.gaim.MainActivity
import com.example.gaim.search.GaimActivity

//
enum class MainpageActivity  (override val activity: Class<out AbstractActivity>): GaimActivity {
    ACCOUNT(AccountActivity::class.java),
    MAIN(MainActivity::class.java);

    companion object {
        fun find(search: Class<out AbstractActivity>): MainpageActivity? {
            val activity =MainpageActivity.entries.find { it.activity == search };
            return activity
        }
    }
}