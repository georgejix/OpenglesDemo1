package com.example.openglesdemo1.ui.normal.t12

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle
import com.example.openglesdemo1.R

class FboFenceActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fbo_fence)
        val glSurfaceView = findViewById<GLSurfaceView>(R.id.glsurfaceview)
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0)
        glSurfaceView.setEGLContextClientVersion(3)
        glSurfaceView.setRenderer(SampleFenceSyncRenderer(findViewById(R.id.image_view)))
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }
}