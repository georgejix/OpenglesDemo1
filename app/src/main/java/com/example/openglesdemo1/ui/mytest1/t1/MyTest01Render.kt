package com.example.openglesdemo1.ui.mytest1.t1

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyTest01Render(val mContext: Context, val mListener: Listener?) : GLSurfaceView.Renderer {

    private val mCameraFilter: CameraFilter by lazy { CameraFilter() }
    private val mColorFilter: ColorFilter by lazy { ColorFilter() }

    fun setMatrix(back: Boolean, p: Float) {
        mCameraFilter.setMatrix(back, p)
        mColorFilter.setMatrix(back, p)
    }

    fun getSurfaceTexture(): SurfaceTexture? = mCameraFilter.mCameraSurfaceTexture

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d(MyTest1Activity.TAG, "onSurfaceCreated")
        mCameraFilter.onSurfaceCreate()
        mColorFilter.onSurfaceCreate()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.d(MyTest1Activity.TAG, "onSurfaceChanged")
        mCameraFilter.onSurfaceChanged(width, height)
        mColorFilter.onSurfaceChanged(width, height)
        mListener?.onOpenCamera()
    }

    override fun onDrawFrame(gl: GL10?) {
        mCameraFilter.onDraw()
        mColorFilter.mColorTextureId = mCameraFilter.mFrameTextureId
        mColorFilter.onDraw()
    }

    interface Listener {
        fun onOpenCamera()
    }
}