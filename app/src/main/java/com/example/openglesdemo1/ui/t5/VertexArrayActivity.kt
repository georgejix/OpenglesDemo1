package com.example.openglesdemo1.ui.t5

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

class VertexArrayActivity : BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return VertexArrayRenderer()
    }
}