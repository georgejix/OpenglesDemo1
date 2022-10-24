package com.example.openglesdemo1.ui.stu3.t1

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

class Test1Activity:BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return Test1Renderer(this)
    }
}