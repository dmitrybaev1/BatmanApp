package com.example.animationsproject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.text.SimpleDateFormat
import java.util.*

class SaveBitmapWorker(context: Context, params: WorkerParameters) : Worker(context,params) {
    private val title = "Blurred Image"
    private val dateFormatter = SimpleDateFormat(
        "yyyy.MM.dd 'at' HH:mm:ss z",
        Locale.getDefault()
    )
    override fun doWork(): Result {
        val resolver = applicationContext.contentResolver
        return try {
            val inputUri = inputData.getString(BlurActivity.BITMAP_KEY)
            val bitmap = BitmapFactory.decodeStream(resolver.openInputStream(
                Uri.parse(inputUri)))
            val imageUrl = MediaStore.Images.Media.insertImage(resolver,bitmap,title,dateFormatter.format(Date()))
            if(!imageUrl.isNullOrEmpty()){
                val outputData = workDataOf(BlurActivity.URL_SAVED_BITMAP_KEY to imageUrl)
                Result.success(outputData)
            }
            else
                Result.failure()
        }
        catch (t: Throwable){
            Result.failure()
        }
    }

}