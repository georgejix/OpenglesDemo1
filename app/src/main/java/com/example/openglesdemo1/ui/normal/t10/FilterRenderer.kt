package com.example.openglesdemo1.ui.normal.t10

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.normal.t10.filter.BaseFilter
import com.example.openglesdemo1.ui.normal.t10.filter.OriginFilter
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class FilterRenderer : GLSurfaceView.Renderer {
    private var mSurfaceWidth: Int = 0
    private var mSurfaceHeight: Int = 0
    private var mTargetFilter: BaseFilter = OriginFilter()


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mTargetFilter.onSurfaceCreated()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        mSurfaceWidth = width
        mSurfaceHeight = height
        mTargetFilter.onSurfaceChanged(mSurfaceWidth, mSurfaceHeight)
    }

    override fun onDrawFrame(gl: GL10?) {
        mTargetFilter.onDrawFrame()
    }

    fun setFilter(baseFilter: BaseFilter) {
        mTargetFilter.onDestroy()
        mTargetFilter = baseFilter
        mTargetFilter.onSurfaceCreated()
        mTargetFilter.onSurfaceChanged(mSurfaceWidth, mSurfaceHeight)
    }
}