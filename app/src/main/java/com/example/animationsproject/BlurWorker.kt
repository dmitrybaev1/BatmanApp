package com.example.animationsproject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class BlurWorker (context: Context, params: WorkerParameters) : Worker(context,params) {
    override fun doWork(): Result {
        return try{
            val inputUri = inputData.getString(BlurActivity.BITMAP_KEY)
            val inBitmap = BitmapFactory.decodeStream(applicationContext.contentResolver.openInputStream(Uri.parse(inputUri)))
            val outBitmap = blurBitmap(inBitmap)
            val outUri = writeBitmapToFile(outBitmap)
            val outputData = workDataOf(BlurActivity.BITMAP_KEY to outUri.toString())
            Result.success(outputData)
        }
        catch(t: Throwable){
            t.printStackTrace()
            Result.failure()
        }
    }
    private fun blurBitmap(bitmap: Bitmap): Bitmap {
        lateinit var rsContext: RenderScript
        try {

            // Create the output bitmap
            val output = Bitmap.createBitmap(
                bitmap.width, bitmap.height, bitmap.config)

            // Blur the image
            rsContext = RenderScript.create(applicationContext, RenderScript.ContextType.DEBUG)
            val inAlloc = Allocation.createFromBitmap(rsContext, bitmap)
            val outAlloc = Allocation.createTyped(rsContext, inAlloc.type)
            val theIntrinsic = ScriptIntrinsicBlur.create(rsContext, Element.U8_4(rsContext))
            theIntrinsic.apply {
                setRadius(10f)
                theIntrinsic.setInput(inAlloc)
                theIntrinsic.forEach(outAlloc)
            }
            outAlloc.copyTo(output)

            return output
        } finally {
            rsContext.finish()
        }
    }
    private fun writeBitmapToFile(bitmap: Bitmap): Uri {
        val name = String.format("blurred-joker-%s.jpg", UUID.randomUUID().toString())
        val outputDir = File(applicationContext.filesDir, BlurActivity.OUTPUT_PATH)
        Log.d("out",outputDir.absolutePath)
        if (!outputDir.exists()) {
            outputDir.mkdirs() // should succeed
        }
        val outputFile = File(outputDir, name)
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(outputFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /* ignored for PNG */, out)
        } finally {
            out?.let {
                try {
                    it.close()
                } catch (ignore: IOException) {
                }

            }
        }
        return Uri.fromFile(outputFile)
    }
}