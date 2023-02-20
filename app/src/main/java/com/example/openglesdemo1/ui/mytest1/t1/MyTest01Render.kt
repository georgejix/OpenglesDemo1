package com.example.openglesdemo1.ui.mytest1.t1

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyTest01Render(val mContext: Context?, val mListener: Listener?) : GLSurfaceView.Renderer {
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        mListener?.onOpenCamera()

    }

    override fun onDrawFrame(gl: GL10?) {

    }

    interface Listener {
        fun onOpenCamera()
    }
}