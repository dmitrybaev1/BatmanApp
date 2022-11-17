package com.example.animationsproject

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File

class CleanupWorker(context: Context,params: WorkerParameters) : Worker(context,params) {
    override fun doWork(): Result {
        return try {
            val outputDirectory = File(applicationContext.filesDir, BlurActivity.OUTPUT_PATH)
            if (outputDirectory.exists()) {
                val entries = outputDirectory.listFiles()
                if (entries != null) {
                    for (entry in entries) {
                        val name = entry.name
                        if (name.isNotEmpty() && name.endsWith(".jpg")) {
                            entry.delete()
                        }
                    }
                }
            }
            Result.success()
        } catch (exception: Exception) {
            exception.printStackTrace()
            Result.failure()
        }
    }

}