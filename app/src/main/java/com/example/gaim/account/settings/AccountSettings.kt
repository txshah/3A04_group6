package com.example.gaim.account.settings

import com.example.gaim.search.SearchResult

//Manages account history UI and interactions
class AccountSettings (private val username: String, private val password: String){

    //associates this instance of account setting sto the given username and password
    init {

    }

    //saves the given animal/accuracy to database of username and password
    fun save(animal: String, accuracy: Float){

    }
    fun save(searchResult: SearchResult){

    }

    //returns all animals associated with the given username and password
    fun getAnimals(): MutableCollection<SearchResult>{
        return mutableSetOf()
    }

}