package com.example.gaim.search.algorithm

import com.example.gaim.search.SearchResult

//see SEARCH ALGORITHM for description
class FreeformSearchAlgorithm : SearchAlgorithm<String>{
    override fun search(input: String): SearchResult {
//        in real implementation use input text
        var sample_input = "The towering beast was covered in a dense, shaggy coat of brown fur, with patches of lighter shades glistening in the sunlight." +
                " Its powerful limbs ended in massive paws, each armed with long, curved claws that dug effortlessly into the earth as it lumbered forward, " +
                "its broad, humped back swaying with every step"
//        extract key words - 10 main api hugging face keyword extraction
//        loop through and query for animals with those key words (add animal to hash and check if it has one of five key words
//        most key words, pick that animal
//        number of key words it has determines accuracy (out of 10)


        return SearchResult(animalName = "Grizzly Bear", accuracy = 0.80)
    }

}

