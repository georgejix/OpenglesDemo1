package com.example.openglesdemo1.ui.t3

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

class RectangleActivity:BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return RectangleRender()
    }
}