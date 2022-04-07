package com.example.openglesdemo1.ui.t12

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class RecordByCameraRender(val mGLSurfaceView: GLSurfaceView, val mListener: Listener) :
    GLSurfaceView.Renderer {
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mListener.onSurfaceCreated()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
    }

    override fun onDrawFrame(gl: GL10?) {
    }

    interface Listener {
        fun onSurfaceCreated()
    }

    fun start() {

    }

    fun stop() {

    }

    fun release() {

    }
}