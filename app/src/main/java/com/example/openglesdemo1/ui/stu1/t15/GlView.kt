package com.example.openglesdemo1.ui.stu1.t15

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.example.openglesdemo1.ui.stu1.t15.filter.BaseFilter

class GlView : GLSurfaceView {
    private val mRender: FilterRenderer by lazy { FilterRenderer() }

    constructor(context: Context) : super(context, null) {
        setupSurfaceView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupSurfaceView()
    }

    private fun setupSurfaceView() {
        setEGLContextClientVersion(3)
        setRenderer(mRender)
        try {
            requestRender()
        } catch (e: Exception) {
        }
    }

    fun setFilter(baseFilter: BaseFilter) {
        queueEvent(object : Runnable {
            override fun run() {
                mRender.setFilter(baseFilter)
            }
        })
        try {
            requestRender()
        } catch (e: Exception) {
        }
    }
}