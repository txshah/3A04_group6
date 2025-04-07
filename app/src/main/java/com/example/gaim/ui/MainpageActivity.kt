package com.example.gaim.ui

import com.example.gaim.MainActivity
import com.example.gaim.search.GaimActivity
import com.example.gaim.ui.account.AccountActivity
import com.example.gaim.ui.account.CreateAccountActivity
import com.example.gaim.ui.account.LoginActivity
import com.example.gaim.ui.search.AnimalReportActivity
import com.example.gaim.ui.search.DisplayResultsActivity

//
enum class MainpageActivity  (override val activity: Class<out AbstractActivity>): GaimActivity {
    ACCOUNT(AccountActivity::class.java),
    MAIN(MainActivity::class.java),
    LOGIN(LoginActivity::class.java),
    CREATEACCOUNT(CreateAccountActivity::class.java),
    DISPLAY(DisplayResultsActivity::class.java),
    REPORT(AnimalReportActivity::class.java);

    companion object {
        fun find(search: Class<out AbstractActivity>): MainpageActivity? {
            val activity =MainpageActivity.entries.find { it.activity == search };
            return activity
        }
    }
}