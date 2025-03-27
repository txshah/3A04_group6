package com.example.gaim2.search.algorithm.database
import java.sql.DriverManager

fun main() {
    // Connect to the database
    val dbPath = "canadian_species.db"
    val connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")

    // Example 1: Simple query
    val statement = connection.createStatement()
    val resultSet = statement.executeQuery("SELECT name, legs, size FROM species LIMIT 5")

    println("First 5 species:")
    while (resultSet.next()) {
        val name = resultSet.getString("name")
        val legs = resultSet.getInt("legs")
        val size = resultSet.getString("size")
        println("$name (Legs: $legs, Size: $size)")
    }

    // Example 2: Parameterized query
    val preparedStatement = connection.prepareStatement(
        "SELECT name FROM species WHERE endangered = ? AND size = ?"
    )
    preparedStatement.setString(1, "Yes")
    preparedStatement.setString(2, "Large")

    val endangeredLarge = preparedStatement.executeQuery()
    println("\nEndangered large species:")
    while (endangeredLarge.next()) {
        println(endangeredLarge.getString("name"))
    }

    // Close resources
    resultSet.close()
    statement.close()
    connection.close()
}