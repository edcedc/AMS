package com.yyc.ams.weight

/**
 * User: Nike
 *  2024/1/2 09:31
 */

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SignalCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var oneRadius = 0
    private var twoRadius = 0
    private var threeRadius = 0
    private var fourRadius = 0
    private var fiveRadius = 0

    private var centerX = 0
    private var centerY = 0

    private val distance = 40

    private val blankDistance = 2

    private var rssi = 0

    private val grayColor = Color.parseColor("#91929E")
    private val whiteColor = Color.parseColor("#FFFFFF")
    private val blueColor = Color.parseColor("#3F8CFF")


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height
        //设置圆
        oneRadius = Math.min(width, height) / 20
        twoRadius = oneRadius + distance
        threeRadius = twoRadius + distance
        fourRadius = threeRadius + distance
        fiveRadius = fourRadius + distance

        //取宽高
        centerX = width / 2
        centerY = height / 2

        when {
            rssi in 1..35 -> {
                setFiveCircleStatus(canvas)
            }
             rssi in 36..49 -> {
                setFourCircleStatus(canvas)
            }
            rssi in 50..59 -> {
                setThreeCircleStatus(canvas)
            }
             rssi in 60..69 -> {
                setTwoCircleStatus(canvas)
            }
            rssi > 70 -> {
                setOneCircleStatus(canvas)
            }
            else -> {
                setDefaultStatus(canvas)
            }
        }

        /* // 设置内部圆
         val mInternalPaint = Paint()
         mInternalPaint.style = Paint.Style.STROKE
         mInternalPaint.color = Color.parseColor("#3F8CFF")
         mInternalPaint.strokeWidth = distance - (blankDistance * 2).toFloat()
         mInternalPaint.isAntiAlias = true
 
         val mFiveLength = fiveRadius - fourRadius - (blankDistance * 2)
         val mFiveRadius = fourRadius + (mFiveLength / 2) + blankDistance
         canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), mFiveRadius.toFloat(), mInternalPaint)
 
         val mFourLength = fourRadius - threeRadius - (blankDistance * 2)
         val mFourRadius = threeRadius + (mFourLength / 2) + blankDistance
         canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), mFourRadius.toFloat(), mInternalPaint)
 
         val mThreeLength = threeRadius - twoRadius - (blankDistance * 2)
         val mThreeRadius = twoRadius + (mThreeLength / 2) + blankDistance
         canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), mThreeRadius.toFloat(), mInternalPaint)
 
         val mTwoLength = twoRadius - oneRadius - (blankDistance * 2)
         val mTwoRadius = oneRadius + (mTwoLength / 2) + blankDistance
         canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), mTwoRadius.toFloat(), mInternalPaint)
 
         //设置实心圆
         val mSolidPaint = Paint()
         mSolidPaint.style = Paint.Style.FILL_AND_STROKE
         mSolidPaint.color = Color.parseColor("#3F8CFF")
         mSolidPaint.isAntiAlias = true
         canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), oneRadius.toFloat() - blankDistance, mSolidPaint)*/


    }

    //默认状态圆
    private fun setDefaultStatus(canvas: Canvas) {
        var paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        paint.color = grayColor
        paint.isAntiAlias = true

        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), oneRadius.toFloat(), paint)
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), twoRadius.toFloat(), paint)
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), threeRadius.toFloat(), paint)
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), fourRadius.toFloat(), paint)
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), fiveRadius.toFloat(), paint)
    }

    //实心圆
    private fun setSolid(canvas: Canvas) {
        val mSolidPaint = Paint()
        mSolidPaint.style = Paint.Style.FILL_AND_STROKE
        mSolidPaint.color = blueColor
        mSolidPaint.isAntiAlias = true
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), oneRadius.toFloat() - blankDistance - 1, mSolidPaint)
    }

    //RSSI<-70
    private fun setOneCircleStatus(canvas: Canvas){
        setDefaultStatus(canvas)
        setSolid(canvas)
    }

    //-69.9<RSSI<60
    private fun setTwoCircleStatus(canvas: Canvas){
        var paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        paint.color = grayColor
        paint.isAntiAlias = true
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), twoRadius.toFloat(), paint)
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), threeRadius.toFloat(), paint)
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), fourRadius.toFloat(), paint)
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), fiveRadius.toFloat(), paint)

        paint.color = whiteColor
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), oneRadius.toFloat(), paint)

        // 设置内部圆
        val mInternalPaint = Paint()
        mInternalPaint.style = Paint.Style.STROKE
        mInternalPaint.color = blueColor
        mInternalPaint.strokeWidth = distance - (blankDistance * 2).toFloat()
        mInternalPaint.isAntiAlias = true
        val mTwoLength = twoRadius - oneRadius - (blankDistance * 2)
        val mTwoRadius = oneRadius + (mTwoLength / 2) + blankDistance
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), mTwoRadius.toFloat(), mInternalPaint)
        setSolid(canvas)
    }

    //-59.9<RSSI<50
    private fun setThreeCircleStatus(canvas: Canvas){
        var paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        paint.color = grayColor
        paint.isAntiAlias = true
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), threeRadius.toFloat(), paint)
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), fourRadius.toFloat(), paint)
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), fiveRadius.toFloat(), paint)

        paint.color = whiteColor
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), oneRadius.toFloat(), paint)
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), twoRadius.toFloat(), paint)

        // 设置内部圆
        val mInternalPaint = Paint()
        mInternalPaint.style = Paint.Style.STROKE
        mInternalPaint.color = blueColor
        mInternalPaint.strokeWidth = distance - (blankDistance * 2).toFloat()
        mInternalPaint.isAntiAlias = true

        val mTwoLength = twoRadius - oneRadius - (blankDistance * 2)
        val mTwoRadius = oneRadius + (mTwoLength / 2) + blankDistance
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), mTwoRadius.toFloat(), mInternalPaint)
        val mThreeLength = threeRadius - twoRadius - (blankDistance * 2)
        val mThreeRadius = twoRadius + (mThreeLength / 2) + blankDistance
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), mThreeRadius.toFloat(), mInternalPaint)

        setSolid(canvas)
    }

    //-49.9<RSSI<35
    private fun setFourCircleStatus(canvas: Canvas){
        var paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        paint.color = grayColor
        paint.isAntiAlias = true
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), fourRadius.toFloat(), paint)
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), fiveRadius.toFloat(), paint)

        paint.color = whiteColor
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), oneRadius.toFloat(), paint)
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), twoRadius.toFloat(), paint)
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), threeRadius.toFloat(), paint)

        // 设置内部圆
        val mInternalPaint = Paint()
        mInternalPaint.style = Paint.Style.STROKE
        mInternalPaint.color = blueColor
        mInternalPaint.strokeWidth = distance - (blankDistance * 2).toFloat()
        mInternalPaint.isAntiAlias = true

        val mTwoLength = twoRadius - oneRadius - (blankDistance * 2)
        val mTwoRadius = oneRadius + (mTwoLength / 2) + blankDistance
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), mTwoRadius.toFloat(), mInternalPaint)
        val mThreeLength = threeRadius - twoRadius - (blankDistance * 2)
        val mThreeRadius = twoRadius + (mThreeLength / 2) + blankDistance
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), mThreeRadius.toFloat(), mInternalPaint)
        val mFourLength = fourRadius - threeRadius - (blankDistance * 2)
        val mFourRadius = threeRadius + (mFourLength / 2) + blankDistance
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), mFourRadius.toFloat(), mInternalPaint)

        setSolid(canvas)
    }

    //0<RSSI<34.9
    private fun setFiveCircleStatus(canvas: Canvas){
        var paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        paint.color = grayColor
        paint.isAntiAlias = true
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), fiveRadius.toFloat(), paint)

        paint.color = whiteColor
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), oneRadius.toFloat(), paint)
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), twoRadius.toFloat(), paint)
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), threeRadius.toFloat(), paint)
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), fourRadius.toFloat(), paint)

        // 设置内部圆
        val mInternalPaint = Paint()
        mInternalPaint.style = Paint.Style.STROKE
        mInternalPaint.color = blueColor
        mInternalPaint.strokeWidth = distance - (blankDistance * 2).toFloat()
        mInternalPaint.isAntiAlias = true

        val mTwoLength = twoRadius - oneRadius - (blankDistance * 2)
        val mTwoRadius = oneRadius + (mTwoLength / 2) + blankDistance
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), mTwoRadius.toFloat(), mInternalPaint)
        val mThreeLength = threeRadius - twoRadius - (blankDistance * 2)
        val mThreeRadius = twoRadius + (mThreeLength / 2) + blankDistance
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), mThreeRadius.toFloat(), mInternalPaint)
        val mFourLength = fourRadius - threeRadius - (blankDistance * 2)
        val mFourRadius = threeRadius + (mFourLength / 2) + blankDistance
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), mFourRadius.toFloat(), mInternalPaint)
        val mFiveLength = fiveRadius - fourRadius - (blankDistance * 2)
        val mFiveRadius = fourRadius + (mFiveLength / 2) + blankDistance
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), mFiveRadius.toFloat(), mInternalPaint)

        setSolid(canvas)
    }

    fun setRssi(rssi: Int) {
        this.rssi = rssi
        invalidate()
    }

}
