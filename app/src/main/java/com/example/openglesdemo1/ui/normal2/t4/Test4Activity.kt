package com.example.openglesdemo1.ui.normal2.t4

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

class Test4Activity : BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return Test4Render(this)
    }
}