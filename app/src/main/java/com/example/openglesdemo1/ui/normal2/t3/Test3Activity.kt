package com.example.openglesdemo1.ui.normal2.t3

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

class Test3Activity : BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return Test3Render(this)
    }
}