package com.example.openglesdemo1.ui.stu1.t11

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import com.example.openglesdemo1.R
import com.example.openglesdemo1.nativegl.NativeGl
import kotlinx.android.synthetic.main.activity_native_opengles.*

class NativeOpenGlesActivity : Activity() {
    private val mNativeGl: NativeGl by lazy { NativeGl() }
    private var mEnable = false
    private val mSurfaceView: SurfaceView by lazy { SurfaceView(this) }
    private val mColors = intArrayOf(
        Color.RED,
        Color.YELLOW,
        Color.BLUE,
        Color.GREEN,
        Color.GRAY,
        Color.BLACK,
        Color.WHITE
    )
    private var mIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_opengles)
        initView()
    }

    private fun initView() {
        t1.setOnClickListener(this::onClick)
        t2.setOnClickListener(this::onClick)
        layout_surface.addView(mSurfaceView)
        mSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                mEnable = true
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }

        })
    }

    private fun onClick(view: View) {
        when (view.id) {
            R.id.t1 -> {
                mNativeGl.drawColor(mSurfaceView.holder.surface, mColors[mIndex++ % mColors.size])
            }
            R.id.t2 -> {
                val options = BitmapFactory.Options()
                options.inScaled = false
                val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.main, options)
                mNativeGl.drawBitmap(mSurfaceView.holder.surface, bitmap)
            }
        }
    }
}