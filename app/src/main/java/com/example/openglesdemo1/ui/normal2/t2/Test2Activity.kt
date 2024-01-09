package com.example.openglesdemo1.ui.normal2.t2

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

class Test2Activity : BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return Test2Render(this)
    }
}