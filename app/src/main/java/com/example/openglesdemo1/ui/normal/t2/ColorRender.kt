package com.example.openglesdemo1.ui.normal.t2

import android.graphics.Color
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ColorRender(private val color: Int) : GLSurfaceView.Renderer {
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val redF: Float = Color.red(color) / 255.0f
        val greenF: Float = Color.green(color) / 255.0f
        val blueF: Float = Color.blue(color) / 255.0f
        val alphaF: Float = Color.alpha(color) / 255.0f
        GLES30.glClearColor(redF, greenF, blueF, alphaF)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GL10.GL_COLOR_BUFFER_BIT)
    }
}