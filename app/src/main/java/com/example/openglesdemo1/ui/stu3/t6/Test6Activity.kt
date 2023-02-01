package com.example.openglesdemo1.ui.stu3.t6

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

class Test6Activity : BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return Test6Render(this)
    }
}