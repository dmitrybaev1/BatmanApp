package com.example.animationsproject

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics

class MeasureTranslator(private val context: Context) {

    fun pxToDp(px: Float) = px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

    fun dpToPx(dp: Float) = dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

    fun spToPx(sp: Float) = sp * (context.resources.displayMetrics.scaledDensity)
}