package com.best.motorica

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MAIN_TAG"
    }

    private lateinit var mCaptureBtn: Button
    private lateinit var mImageView: ImageView

    private var image_uri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mImageView = findViewById(R.id.image_view)
        mCaptureBtn = findViewById(R.id.capture_image_btn)

        //button click
        mCaptureBtn.setOnClickListener(View.OnClickListener {
            //Camera is clicked we need to check if we have permission of Camera, Storage before launching Camera to Capture image
            if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                //Device version is TIRAMISU or above. We only need Camera permission
                val cameraPermissions = arrayOf(Manifest.permission.CAMERA)
                requestCameraPermissions.launch(cameraPermissions)
            } else {
                //Device version is below TIRAMISU. We need Camera & Storage permissions
                val cameraPermissions =
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestCameraPermissions.launch(cameraPermissions)
            }
        })
    }

    private val requestCameraPermissions =
        registerForActivityResult<Array<String>, Map<String, Boolean>>(
            ActivityResultContracts.RequestMultiplePermissions(),
            ActivityResultCallback<Map<String, Boolean>> { result ->
                Log.d(TAG, "onActivityResult: ")
                Log.d(TAG, "onActivityResult: $result")

                //let's check if permissions are granted or not
                var areAllGranted = true
                for (isGranted in result.values) {
                    areAllGranted = areAllGranted && isGranted
                }
                if (areAllGranted) {
                    //All Permissions Camera, Storage are granted, we can now launch camera to capture image
                    pickImageCamera()
                } else {
                    //Camera or Storage or Both permissions are denied, Can't launch camera to capture image

                    Toast.makeText(
                        this@MainActivity,
                        "Camera or Storage or both permissions denied...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

    private fun pickImageCamera() {
        Log.d(TAG, "pickImageCamera: ")
        //Setup Content values, MediaStore to capture high quality image using camera intent
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "TEMPORARY_IMAGE")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "TEMPORARY_IMAGE_DESCRIPTION")
        //Uri of the image to be captured from camera
        image_uri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        //Intent to launch camera
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "onActivityResult: ")
        //Check if image is picked or not
        if (result.resultCode == RESULT_OK) {
            //no need to get image uri here we will have it in pickImageCamera() function
            Log.d(TAG, "onActivityResult: imageUri: $image_uri")
        } else {
            //Cancelled
            Toast.makeText(this@MainActivity, "Cancelled...!", Toast.LENGTH_SHORT).show()
        }
    }



    fun onClickToast(View: View) {
        Toast.makeText(this, "Когда-нибудь здесь будет навигация...", Toast.LENGTH_SHORT).show()
    }


}
