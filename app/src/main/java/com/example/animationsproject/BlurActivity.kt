package com.example.animationsproject

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.work.*
import com.example.animationsproject.databinding.ActivityBlurBinding
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class BlurActivity : AppCompatActivity() {
    companion object{
        const val BITMAP_KEY = "bitmap"
        const val URL_SAVED_BITMAP_KEY = "url saved bitmap"
        const val BLUR_IMAGE_WORK = "blur image work"
        const val BLUR_TAG = "blur tag"
        const val SAVE_IMAGE_TAG = "save image tag"
        const val OUTPUT_PATH = "blur_outputs"
    }
    private lateinit var binding: ActivityBlurBinding
    private val workManager = WorkManager.getInstance(this)
    private var outputUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlurBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.applyButton.setOnClickListener {
            var continuation = workManager.beginUniqueWork(BLUR_IMAGE_WORK,ExistingWorkPolicy.KEEP,
                OneTimeWorkRequestBuilder<BlurWorker>()
                    .addTag(BLUR_TAG)
                    .setInputData(Data.Builder()
                        .putString(BITMAP_KEY,getImageUri().toString())
                        .build())
                    .build()
            )

            val saveBitmapRequest = OneTimeWorkRequestBuilder<SaveBitmapWorker>()
                .addTag(SAVE_IMAGE_TAG)
                .build()

            continuation = continuation.then(saveBitmapRequest)

            val cleanupRequest = OneTimeWorkRequestBuilder<CleanupWorker>()
                .build()

            continuation = continuation.then(cleanupRequest)
            continuation.enqueue()

            waitForBlurredBitmapAndSet()
            waitForSavingBitmapAndShowButton()
        }
        binding.seeImageButton.setOnClickListener {
            outputUri?.let{
                val intent = Intent(Intent.ACTION_VIEW,it)
                startActivity(intent)
            }
        }
    }
    private fun waitForBlurredBitmapAndSet(){
        workManager.getWorkInfosByTagLiveData(BLUR_TAG).observe(this) {
            val workInfo = it[0]
            if(workInfo.state.isFinished){
                val imageUri = workInfo.outputData.getString(BITMAP_KEY)
                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(Uri.parse(imageUri)))
                binding.jokerImageView.setImageBitmap(bitmap)
            }
        }
    }
    private fun waitForSavingBitmapAndShowButton(){
        Log.d("enter","ENTER TO WAIT FOR SAVING")
        workManager.getWorkInfosByTagLiveData(SAVE_IMAGE_TAG).observe(this){
            val workInfo = it[0]
            if(workInfo.state.isFinished){
                val resultUri = workInfo.outputData.getString(URL_SAVED_BITMAP_KEY)
                outputUri = resultUri?.toUri()
                binding.seeImageButton.visibility = View.VISIBLE
            }
        }
    }

    private fun getImageUri(): Uri =
        Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(R.drawable.joker_to_blend))
            .appendPath(resources.getResourceTypeName(R.drawable.joker_to_blend))
            .appendPath(resources.getResourceEntryName(R.drawable.joker_to_blend))
            .build()

    override fun onStop() {
        super.onStop()
        workManager.cancelAllWork()
    }
}