package com.example.gaim.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView

import androidx.activity.enableEdgeToEdge
import com.example.gaim.R
import com.example.gaim.search.algorithm.ImageSearchAlgorithm
import com.example.gaim.search.algorithm.SearchAlgorithm
import com.example.gaim.ui.MainpageActivity
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.lifecycleScope
import com.example.gaim.search.algorithm.SurveySearchAlgorithm
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
//        DEBUG REFERENCES
//Output Stream: https://stackoverflow.com/questions/55312954/saving-file-in-internal-storage-using-uri-obtained-from-storage-access-network/55313801
//Photo picker: https://developer.android.com/training/data-storage/shared/photopicker


//image front end to allow user to update photo
class ImageSearchActivity: AbstractSearchActivity<String> () {
    private val TAG = "ImageSearchActivity"
//    set up algorithms to allow for search calls
    override val algorithm: SearchAlgorithm<String> by lazy {
        ImageSearchAlgorithm(this)
    }
//    uploading xml connection
    private val uploadImageId = R.id.upload_image

    private val PICK_IMAGE_REQUEST = 1001
    private val imagePreviewID = R.id.ivPreview
//    image path
    private var selectedImagePath: String? = null

//on create - at beginning
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//    follow this made view
        setContentView(R.layout.activity_image_search)
        Log.d(TAG, "ImageSearchActivity created")

//upload image button set up
        val uploadImage = findViewById<Button>(uploadImageId)
        Log.d(TAG, "UI buttons initialized")

//when you click get URI to get location - this image from images in phone
        uploadImage.setOnClickListener {
            Log.d(TAG, "Upload Image button clicked")
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        get code for image
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data!!
            findViewById<ImageView>(imagePreviewID).setImageURI(imageUri)
//URI to filepath (get location of stored on phone)
            val filePath = saveImageToInternalStorage(imageUri)
            Log.d(TAG, "HERE $filePath")
            if (filePath != null) {
                selectedImagePath = filePath.toString()
                Log.d(TAG, "Image saved to: $filePath")
                Log.d(TAG, "Does file exist? ${File(filePath).exists()}")
                Log.d(TAG, "File size: ${File(filePath).length()} bytes")
                Log.d(TAG, "Saved path: $filePath")
//                use this to make sure API run from iamge location - need to use @due to suspend class (added due to api sage)
                lifecycleScope.launch {
                    completeSearch(filePath.toString(), this@ImageSearchActivity)
                }
            } else {
                Log.e(TAG, "Failed to save image to internal storage")
            }
        }
    }

//    private fun imageAnalyzed(): Boolean{
//        Log.d(TAG, "Starting image analysis")
//        //ADD IMAGE ANALYSIS
////        val result = algorithm.search("")
//        Log.d(TAG, "Analysis result: $result")
//
//        // Access the raw API response from the algorithm
//        val rawResponse = (algorithm as ImageSearchAlgorithm).getLastRawResponse()
//        val statusCode = (algorithm as ImageSearchAlgorithm).getLastStatusCode()
//
//        // Log the raw response
//        Log.d(TAG, "Raw API response status code: $statusCode")
//        Log.d(TAG, "Raw API response: $rawResponse")
//
//        // You could show this in a dialog or another view if needed
//        // showRawResponseDialog(rawResponse)
//
//        Log.d(TAG, "Image analysis completed")
//        return true
//    }
//function to allow inbuild to storage
    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
//            set to time saved to ensure unique name for image at all time
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMG_$timeStamp.jpg"

            val directory = File(filesDir, "account/database")
            if (!directory.exists()) directory.mkdirs()
//save to director using output and input streams
            val file = File(directory, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error saving image: ${e.message}", e)
            null
        }
    }
}