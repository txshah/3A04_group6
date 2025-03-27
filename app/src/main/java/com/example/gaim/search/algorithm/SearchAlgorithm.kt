package com.example.gaim.search.algorithm

import com.example.gaim.search.SearchResult

//receives relevant input type and returns a SEARCH RESULT based on input received
interface SearchAlgorithm <InputType>{
    fun search(input: InputType): SearchResult;
}
