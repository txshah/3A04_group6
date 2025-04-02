package com.example.gaim.search.algorithm

import com.example.gaim.search.SearchResult
import java.sql.DriverManager


//see SEARCH ALGORITHM for description
class FreeformSearchAlgorithm : SearchAlgorithm<String>{
//    global vars - later
//    private val client = OkHttpClient()
//    private val apiToken = ""
//    private val modelUrl = ""

    override fun search(input: String): SearchResult {
//        in real implementation use input text
        var sampleInput = "The towering beast was covered in a dense, shaggy coat of brown fur, with patches of lighter shades glistening in the sunlight." +
                " Its powerful limbs ended in massive paws, each armed with long, curved claws that dug effortlessly into the earth as it lumbered forward, " +
                "its broad, humped back swaying with every step"
//        extract key words - 10 main api hugging face keyword extraction
        var keyWords = extract(sampleInput)
        var options  = query(keyWords)

//        most key words, pick that animal
//        number of key words it has determines accuracy (out of 10)
//        max key value pair
//                name = key
        if (options.isEmpty()){
            return SearchResult("N/A", 0.0)
        }

        var maxEntry = options.maxBy { it.value }
        var species = maxEntry.key
        var accuracy = maxEntry.value.toDouble() / 10
        print("$species and $accuracy")

        return SearchResult(species, accuracy)
    }

    private fun extract(input: String): List<String>{
//        exactly 10 keywords from any input text
//        in one line split input into words and then get all words great than three and take 10 distinct
        var output = input.split(" ")
            .filter { it.length > 3 && it.matches(Regex("[a-zA-Z]+")) }
            .map { it.lowercase() }
            .distinct()
            .take(10)

        print("$output")

        return output
    }

    private fun query(input: List<String>): Map<String, Int>{
        //        loop through and query for animals with those key words (add animal to hash and check if it has one of five key words
//        returns animal and number of key words the animal gets
//        for i in options:
//            search in db - example (KEY WORD COL IS A STRING OF KEYWORDS)
//        return ANIMAL: number of words from keyword list that are in that animals keyword col
        Class.forName("org.sqlite.JDBC")
        val dbPath = "app/src/main/java/com/example/gaim/search/algorithm/database/canadian_species.db"

        val connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
        var output = mutableMapOf<String, Int>()

        for (i in input){
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT name FROM species WHERE keywords LIKE '%$i%'")

            while (resultSet.next()) {
//                number of keywords that animal contains
                val speciesName = resultSet.getString("name")
                output[speciesName] = output.getOrDefault(speciesName, 0) + 1
            }
        }

        return output

    }

}

fun main() {
    val searcher = FreeformSearchAlgorithm()
    val result = searcher.search("") // Input doesn't matter since sampleInput is hardcoded
}
