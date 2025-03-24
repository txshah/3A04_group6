package com.example.gaim2.search.algorithm

//receives relevant input type and returns a SEARCH RESULT based on input received
interface SearchAlgorithm <InputType>{
    fun search(input: InputType): SearchResult;
}

interface Input<out T>{

}

class SearchResult