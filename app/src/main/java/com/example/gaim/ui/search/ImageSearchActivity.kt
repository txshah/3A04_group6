package com.example.gaim.ui.search

import android.os.Bundle
import com.example.gaim.search.algorithm.ImageSearchAlgorithm
import com.example.gaim.search.algorithm.SearchAlgorithm

class ImageSearchActivity: AbstractSearchActivity<String> () {
    override val algorithm: SearchAlgorithm<String> = ImageSearchAlgorithm();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TODO("Not yet implemented")
    }
}