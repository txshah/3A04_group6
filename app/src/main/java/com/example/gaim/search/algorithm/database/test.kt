import java.sql.DriverManager

fun main() {
    try {
        // Load SQLite JDBC driver
        Class.forName("org.sqlite.JDBC")

        // Define the database path - use absolute path for certainty
        val dbPath = "app/src/main/java/com/example/gaim/search/algorithm/database/canadian_species.db"
        val connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")

        println("Connected to db: $dbPath")

        // check tables
        val metaStatement = connection.createStatement()
        val tables = metaStatement.executeQuery("SELECT name FROM sqlite_master WHERE type='table'")
        println("Tables in db:")
        while (tables.next()) {
            println("${tables.getString("name")}")
        }
        tables.close()
        metaStatement.close()

        try {
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT name, legs, size FROM species LIMIT 5")

            println("\nresults:")
            while (resultSet.next()) {
                val name = resultSet.getString("name")
                val legs = resultSet.getInt("legs")
                val size = resultSet.getString("size")
                println("Name: $name, Legs: $legs, Size: $size")
            }

            resultSet.close()
            statement.close()
        } catch (e: Exception) {
            println("Query failed: ${e.message}")
        }

        connection.close()
    } catch (e: ClassNotFoundException) {
        println("SQLite driver not found")
        e.printStackTrace()
    } catch (e: Exception) {
        println("Database connection failed: ${e.message}")
        e.printStackTrace()
    }
}