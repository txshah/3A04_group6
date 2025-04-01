package com.example.gaim.search.algorithm

import com.example.gaim.search.SearchResult
import java.sql.DriverManager

//see SEARCH ALGOIRTHM for description
class SurveySearchAlgorithm : SearchAlgorithm<String>{

    override fun search(input: String): SearchResult {
//        in real implementation use input text
        var sampleInput = "legs:4;coat:Fur; colour:Brown; domain:Land; region:Canada; size:Large"
        var variables= extract(sampleInput)
        var options  = query(variables)

        if (options.isEmpty()){
            print("N/A and 0.0")
            return SearchResult("N/A", 0.0)
        }

        var maxEntry = options.maxBy { it.value }
        var species = maxEntry.key
        var accuracy = maxEntry.value.toDouble()/ (options.size)
        print("$species and $accuracy")

        return SearchResult(species, accuracy)
    }

    private fun extract(input: String): HashMap<String, String>{
//        exactly 10 keywords from any input text
//        in one line split input into words and then get all words great than three and take 10 distinct
        val hashMap = HashMap<String, String>()
        input.split(";")
            .map { it.trim() }
            .forEach { pair ->
                val (key, value) = pair.split(":", limit = 2).map { it.trim() }
                hashMap[key] = value
            }

        return hashMap
    }

    private fun query(input: HashMap<String, String>): Map<String, Int>{
        //        loop through and query for animals with those key words (add animal to hash and check if it has one of five key words
//        returns animal and number of key words the animal gets
//        for i in options:
//            search in db - example (KEY WORD COL IS A STRING OF KEYWORDS)
//        return ANIMAL: number of words from keyword list that are in that animals keyword col
        Class.forName("org.sqlite.JDBC")
        val dbPath = "app/src/main/java/com/example/gaim/search/algorithm/database/canadian_species.db"
        val connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
        var output = mutableMapOf<String, Int>()

        var legs = (input["legs"])?.toIntOrNull()  ;
        var coat = input["coat"] ;
        var colour = input["colour"] ;
        var domain = input["domain"] ;
        var region = input["region"] ;
        var size = input["size"] ;

        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT name FROM species WHERE legs=$legs " +
                "and coat='$coat' and colour='$colour' and domain='$domain' and " +
                "region='$region' and size='$size'")

        while (resultSet.next()) {
//                number of keywords that animal contains
            val speciesName = resultSet.getString("name")
            output[speciesName] = 100
        }

        return output

    }

}

fun main() {
    val searcher = SurveySearchAlgorithm()
    val result = searcher.search("") // Input doesn't matter since sampleInput is hardcoded
}
