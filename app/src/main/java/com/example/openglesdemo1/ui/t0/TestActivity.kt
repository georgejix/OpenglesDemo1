package com.example.openglesdemo1.ui.t0

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

class TestActivity:BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return TestRenderer(this)
    }
}