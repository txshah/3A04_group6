package com.example.gaim.ui.search

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView

import androidx.activity.enableEdgeToEdge
import com.example.gaim.R
import com.example.gaim.search.algorithm.ImageSearchAlgorithm
import com.example.gaim.search.algorithm.SearchAlgorithm
import com.example.gaim.ui.MainpageActivity


class ImageSearchActivity: AbstractSearchActivity<String> () {
    private val TAG = "ImageSearchActivity"
    override val algorithm: SearchAlgorithm<String> = ImageSearchAlgorithm();
    private val imagePreviewID = R.id.ivPreview
    private val chooseImageID = R.id.choose_image
    private val uploadImageId = R.id.upload_image

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_image_search)
        Log.d(TAG, "ImageSearchActivity created")

        val imagePreview = findViewById<ImageView>(imagePreviewID)
        Log.d(TAG, "Image preview view initialized")

        val chooseImage = findViewById<Button>(chooseImageID)
        val uploadImage = findViewById<Button>(uploadImageId)
        Log.d(TAG, "UI buttons initialized")

        uploadImage.setOnClickListener {
            Log.d(TAG, "Upload image button clicked")
            if(imageAnalyzed()){
                Log.i(TAG, "Image successfully analyzed, navigating to main activity")
                nextActivity(MainpageActivity.MAIN)
            } else {
                Log.w(TAG, "Image analysis unsuccessful")
            }
        }
        
        chooseImage.setOnClickListener {
            Log.d(TAG, "Choose image button clicked")
            // Existing functionality here
        }
    }

    private fun imageAnalyzed(): Boolean{
        Log.d(TAG, "Starting image analysis")
        //ADD IMAGE ANALYSIS
        val result = algorithm.search("")
        Log.d(TAG, "Analysis result: $result")
        
        // Access the raw API response from the algorithm
        val rawResponse = (algorithm as ImageSearchAlgorithm).getLastRawResponse()
        val statusCode = (algorithm as ImageSearchAlgorithm).getLastStatusCode()
        
        // Log the raw response
        Log.d(TAG, "Raw API response status code: $statusCode")
        Log.d(TAG, "Raw API response: $rawResponse")
        
        // You could show this in a dialog or another view if needed
        // showRawResponseDialog(rawResponse)
        
        Log.d(TAG, "Image analysis completed")
        return true
    }
}