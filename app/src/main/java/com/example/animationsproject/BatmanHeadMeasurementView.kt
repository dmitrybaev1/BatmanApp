package com.example.animationsproject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt

class BatmanHeadMeasurementView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var mHeadColor: Int
    private var mHeadCount: Int
    private val measureTranslator = MeasureTranslator(context)
    private val bitmap: Bitmap
    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.BatmanHeadMeasurementView,
            0, 0).apply {

            try {
                mHeadColor = getColor(R.styleable.BatmanHeadMeasurementView_headColor,ContextCompat.getColor(context,R.color.black))
                mHeadCount = getInt(R.styleable.BatmanHeadMeasurementView_headCount,1)
                if(mHeadCount>100)
                    mHeadCount=100
            } finally {
                recycle()
            }
        }
        bitmap = BitmapFactory.decodeResource(context.resources,R.drawable.batman_head)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val defHeight = if(layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT)
            MeasureSpec.makeMeasureSpec(measureTranslator.dpToPx(25f).roundToInt(),MeasureSpec.EXACTLY)
        else
            heightMeasureSpec
        val defWidth = if(layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT)
            MeasureSpec.makeMeasureSpec(measureTranslator.dpToPx(25f).roundToInt()*mHeadCount,MeasureSpec.EXACTLY)
        else
            widthMeasureSpec
        super.onMeasure(defWidth, defHeight)
    }

    private val headPaint = Paint().apply {
        color = mHeadColor
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.apply {
            for(i in 1..mHeadCount)
                drawHead(canvas,i)
        }
    }
    private fun drawHead(canvas: Canvas,count: Int){
        var startX = x + paddingLeft
        var endX = x + (width/mHeadCount) - paddingRight
        if(count>1) {
            startX = (endX + paddingRight) * (count-1) + paddingLeft
            endX = startX - paddingLeft + (width/mHeadCount) - paddingRight
        }
        val startY = y + paddingTop
        val endY = y + height - paddingBottom
        //canvas.drawOval(startX,startY,endX,endY,headPaint)
        val bitmapRect = Rect(0,0,bitmap.width,bitmap.height)
        val viewRect = Rect(startX.roundToInt(),startY.roundToInt(),endX.roundToInt(),endY.roundToInt())
        canvas.drawBitmap(bitmap,bitmapRect,viewRect,headPaint)
    }
}