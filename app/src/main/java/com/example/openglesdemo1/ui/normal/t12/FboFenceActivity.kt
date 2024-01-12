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

    /**
     * frame buffer本身并没有什么实际内容，它是通过将它的各种attachment给绑定相应的对象而实现相应的功能，
     * 对应渲染内容来说，就是color attachment，可以通过glFramebufferTexture2D()将texture绑定到color
     * attachment上，这时绑定这个frame buffer进行渲染，就会渲染到绑定的texture。
     */
}