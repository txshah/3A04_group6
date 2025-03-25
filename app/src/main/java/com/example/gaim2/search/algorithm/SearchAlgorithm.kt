package com.example.gaim2.search.algorithm

import com.example.gaim2.search.SearchResult

//receives relevant input type and returns a SEARCH RESULT based on input received
interface SearchAlgorithm <InputType>{
    fun search(input: InputType): SearchResult;
}
