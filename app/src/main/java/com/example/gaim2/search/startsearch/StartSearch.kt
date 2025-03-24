package com.example.gaim2.search.startsearch

import com.example.gaim2.search.algorithm.SearchAlgorithm
import com.example.gaim2.search.algorithm.SearchResult
import com.example.gaim2.ui.theme.SearchPage

//takes input data based on the search type requested and repackages it to the correct data type
//then passes correctly typed data to search algorithm
//when search algorithm returns result, it repasses that to SearchController
abstract class StartSearch <InputType>{
    abstract val searchAlgorithm: SearchAlgorithm<InputType>;
    abstract val searchUI: SearchPage //GENERAL UI PAGE

    //SEARCH CONTROLLER will trigger this function which search type is selected, and then start search will trigger the UI page,
    //it then receives the information from UI page, and packages it into the correct InputType, then it passes the
    //type-correct input to the SEARCH ALGORITHM, when the algorithm returns a SEARCH RESULT then START SEARCH will pass
    //the SEARCH RESULT to the SEARCH CONTROLLER
    //***ESSENTIALLY*** it runs the entire search from UI page to passing info to the algorithm to returning it to searchController
    fun runSearch(): SearchResult{
        val information = runDisplay()

        return searchAlgorithm.search(information);
    };

    //runs the UI and returns the information in the correct input type
    protected abstract fun runDisplay(): InputType;

}