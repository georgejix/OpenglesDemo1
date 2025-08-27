package com.example.openglesdemo1.ui.camera.t13

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.example.openglesdemo1.BaseApplication
import com.example.openglesdemo1.R
import java.text.SimpleDateFormat


class WaterMarkUtil {
    private val mCarInfoIconPaint by lazy {
        Paint().also {
            it.isAntiAlias = true
            it.style = Paint.Style.FILL_AND_STROKE
        }
    }
    private val mCarInfoTextPaint by lazy {
        Paint().also {
            it.isAntiAlias = true
            it.style = Paint.Style.FILL_AND_STROKE
            it.textSize = 30f
            it.color = Color.WHITE
        }
    }
    private val mSdf by lazy { SimpleDateFormat("yyyy-MM-dd hh:mm:ss") }

    fun genCarInfoBitmap(): Bitmap {
        val width = 1920
        val height = 108
        val mIconWidth = 56
        val mIconHeight = 56
        val mMarginStart = 20
        val mMarginEnd = 36
        val mIconMarginStart = 20

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawARGB(128, 0, 0, 0)
        BaseApplication.mContext?.let { context ->
            listOf(R.mipmap.icon_acc, R.mipmap.icon_abs, R.mipmap.icon_aeb)
                .forEachIndexed { index, icon ->
                    val options: BitmapFactory.Options = BitmapFactory.Options()
                    options.inScaled = false
                    val icon =
                        BitmapFactory.decodeResource(context.resources, icon, options)
                    val start =
                        mMarginStart + mIconMarginStart + index * (mIconWidth + mMarginStart)
                    canvas.drawBitmap(
                        icon,
                        Rect(0, 0, options.outWidth, options.outHeight),
                        Rect(
                            start, height / 2 - mIconHeight / 2, start + mIconWidth,
                            height / 2 + mIconHeight / 2
                        ), mCarInfoIconPaint
                    )
                }
        }
        val timeBounds = getTextBounds("0000-00-00 00:00:00")
        canvas.drawText(
            mSdf.format(System.currentTimeMillis()),
            width * 1f - timeBounds.width() - mMarginEnd,
            height / 2 + timeBounds.height() / 2f,
            mCarInfoTextPaint
        )
        return bitmap
    }

    private fun getTextBounds(text: String): Rect {
        val textBounds = Rect()
        mCarInfoTextPaint.getTextBounds(text, 0, text.length, textBounds)
        return textBounds
    }
}