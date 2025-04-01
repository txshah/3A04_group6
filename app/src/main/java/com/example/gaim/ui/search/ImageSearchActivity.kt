package com.example.gaim.ui.search

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

import androidx.activity.enableEdgeToEdge
import com.example.gaim.R
import com.example.gaim.search.algorithm.ImageSearchAlgorithm
import com.example.gaim.search.algorithm.SearchAlgorithm
import com.example.gaim.ui.MainpageActivity


class ImageSearchActivity: AbstractSearchActivity<String> () {
    override val algorithm: SearchAlgorithm<String> = ImageSearchAlgorithm();
    private val imagePreviewID = R.id.ivPreview
    private val chooseImageID = R.id.choose_image
    private val uploadImageId = R.id.upload_image

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_image_search)

        val imagePreview = findViewById<ImageView>(imagePreviewID)

        val chooseImage = findViewById<Button>(chooseImageID)
        val uploadImage = findViewById<Button>(uploadImageId)

        uploadImage.setOnClickListener {
            if(imageAnalyzed()){
                nextActivity(MainpageActivity.MAIN)
            }
        }
    }

    private fun imageAnalyzed(): Boolean{
        //ADD IMAGE ANALYSIS
        return true
    }
}