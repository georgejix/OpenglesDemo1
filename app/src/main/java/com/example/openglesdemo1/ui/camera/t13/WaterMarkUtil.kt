package com.example.openglesdemo1.ui.camera.t13

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
            it.textSize = 25f
            it.color = Color.WHITE
        }
    }
    private val mSdf by lazy { SimpleDateFormat("yyyy-MM-dd mm:hh:ss") }

    fun genCarInfoBitmap(): Bitmap {
        val width = 936
        val height = 68
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawARGB(128, 0, 0, 0)
        BaseApplication.mContext?.let { context ->
            var options: BitmapFactory.Options = BitmapFactory.Options()
            options.inScaled = false
            val icon = BitmapFactory.decodeResource(context.resources, R.mipmap.icon_acc, options)
            canvas.drawBitmap(icon, 0f, 0f, mCarInfoIconPaint)
        }
        canvas.drawText(mSdf.format(System.currentTimeMillis()), 700f, 45f, mCarInfoTextPaint)
        return bitmap
    }
}