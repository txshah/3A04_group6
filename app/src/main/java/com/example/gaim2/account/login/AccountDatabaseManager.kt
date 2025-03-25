package com.example.gaim2.account.login

import com.example.gaim2.account.Account

//registers Accounts to the account database and fetches from the database, essentially a class to interface with the
//Account database
class AccountDatabaseManager {
    //returns true if registration successful, false if unsuccessful
    public fun register(account: Account): Boolean{
        TODO("Not yet implemented")
    }

    //potentially can add more account getters in future if there is need
    public fun getAccount(username: String): Account{
        TODO("Not yet implemented")
    }
}