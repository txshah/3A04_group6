package com.example.gaim.search.algorithm

import com.example.gaim.search.SearchResult

//receives relevant input type and returns a SEARCH RESULT based on input received
interface SearchAlgorithm <InputType>{
//    input of type InputType and output is an instance of search result class
//    using suspend because that is required for image search to work (android api requirements)
    suspend fun search(input: InputType): SearchResult
}
