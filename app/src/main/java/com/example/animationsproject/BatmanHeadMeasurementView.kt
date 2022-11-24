package com.example.animationsproject

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt

class BatmanHeadMeasurementView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var mHeadColor: Int
    private var mHeadCount: Int
    private var mRatingMode: Boolean
    private var mRating: Int
    private var mRatingDisabledColor: Int
    private val mBitmap: Bitmap
    private val mHeadPaint = Paint()
    private val mRatingDisabledHeadPaint = Paint()
    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.BatmanHeadMeasurementView,
            0, 0).apply {

            try {
                mHeadColor = getColor(R.styleable.BatmanHeadMeasurementView_headColor,ContextCompat.getColor(context,R.color.black))
                mHeadCount = getInt(R.styleable.BatmanHeadMeasurementView_headCount,1)
                mRatingMode = getBoolean(R.styleable.BatmanHeadMeasurementView_ratingMode,false)
                mRating = getInt(R.styleable.BatmanHeadMeasurementView_rating,0)
                mRatingDisabledColor = getColor(R.styleable.BatmanHeadMeasurementView_ratingDisabledColor,ContextCompat.getColor(context,R.color.gray))
                if(mHeadCount>100)
                    mHeadCount=100
                if(mRating>mHeadCount)
                    mRating=mHeadCount
            } finally {
                recycle()
            }
        }
        mHeadPaint.colorFilter = PorterDuffColorFilter(mHeadColor,PorterDuff.Mode.SRC_IN)
        mRatingDisabledHeadPaint.colorFilter = PorterDuffColorFilter(mRatingDisabledColor,PorterDuff.Mode.SRC_IN)
        mBitmap = BitmapFactory.decodeResource(context.resources,R.drawable.batman_head)
    }

    fun getHeadColor() = mHeadColor

    fun setHeadColor(color: Int){
        mHeadColor = color
        invalidate()
    }

    fun getHeadCount() = mHeadCount

    fun setHeadCount(headCount: Int){
        mHeadCount = headCount
        invalidate()
        requestLayout()
    }

    fun isRatingMode() = mRatingMode

    fun setRatingMode(ratingMode: Boolean){
        mRatingMode = ratingMode
        invalidate()
    }

    fun getRating() = mRating

    fun setRating(rating: Int){
        mRating = rating
        invalidate()
    }

    fun getRatingDisabledColor() = mRatingDisabledColor

    fun setRatingDisabledColor(color: Int){
        mRatingDisabledColor = color
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val defHeight = if(layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT)
            MeasureSpec.makeMeasureSpec(dpToPx(25f,context).roundToInt(),MeasureSpec.EXACTLY)
        else
            heightMeasureSpec
        val defWidth = if(layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT)
            MeasureSpec.makeMeasureSpec(dpToPx(25f,context).roundToInt()*mHeadCount,MeasureSpec.EXACTLY)
        else
            widthMeasureSpec
        super.onMeasure(defWidth, defHeight)
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
        val bitmapRect = Rect(0,0,mBitmap.width,mBitmap.height)
        val viewRect = Rect(startX.roundToInt(),startY.roundToInt(),endX.roundToInt(),endY.roundToInt())
        if(mRatingMode)
            if(count<=mRating)
                canvas.drawBitmap(mBitmap,bitmapRect,viewRect,mHeadPaint)
            else
                canvas.drawBitmap(mBitmap,bitmapRect,viewRect,mRatingDisabledHeadPaint)
        else
            canvas.drawBitmap(mBitmap,bitmapRect,viewRect,mHeadPaint)
    }
}