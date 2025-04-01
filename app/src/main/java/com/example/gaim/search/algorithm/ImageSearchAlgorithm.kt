package com.example.gaim.search.algorithm

import com.example.gaim.search.SearchResult

import java.io.File
import org.jetbrains.kotlinx.dl.api.inference.loaders.ONNXModelHub
import org.jetbrains.kotlinx.dl.api.inference.onnx.ONNXModels
import java.net.URISyntaxException
import java.net.URL


//see SEARCH ALGORITHM for description, input string is file path to image

class ImageSearchAlgorithm : SearchAlgorithm<String> {
    override fun search(input: String): SearchResult {
//        in real implementation use input text
        print("start")
        var sampleInput = "app/src/main/java/com/example/gaim/account/database/redfox.jpg"
        print("api")
        var answer = apiCall(sampleInput)

        if (answer.isEmpty()) {
            return SearchResult("N/A", 0.0)
        }

        var maxEntry = answer.maxBy { it.value }
        var species = maxEntry.key
        var accuracy = maxEntry.value.toDouble()
        print("$species and $accuracy")

        return SearchResult(species, accuracy)
    }

    @Throws(URISyntaxException::class)
    fun getFileFromResource(fileName: String): File {
        val classLoader: ClassLoader = object {}.javaClass.classLoader
        val resource: URL? = classLoader.getResource(fileName)
        return if (resource == null) {
            throw IllegalArgumentException("File not found! $fileName")
        } else {
            File(resource.toURI())
        }
    }

    private fun apiCall(filePath: String): Map<String, Float> {
//        exactly 10 keywords from any input text
//        in one line split input into words and then get all words great than three and take 10 distinct
        print("here")
        val hashMap = HashMap<String, Float>()
        val modelHub = ONNXModelHub(cacheDirectory = File("app/cache/pretrainedModels"))
        print("here34")
        val model = modelHub.loadPretrainedModel(ONNXModels.ObjectDetection.SSD)
        print("here34")
        model.use { detectionModel ->
            println(detectionModel)

            val imageFile = getFileFromResource(filePath)
            val detectedObjects = detectionModel.detectObjects(imageFile = imageFile, topK = 1)

            detectedObjects.forEach {
                hashMap[it.classLabel] = it.probability
                println("Found ${it.classLabel} with probability ${it.probability}")
            }

            print("here1")

            return hashMap
        }

    }
}

fun main() {

    val searcher = ImageSearchAlgorithm()
    val result = searcher.search("") // Input doesn't matter since sampleInput is hardcoded
}



