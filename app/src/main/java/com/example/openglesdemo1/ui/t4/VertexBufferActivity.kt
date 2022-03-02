package com.example.openglesdemo1.ui.t4

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

class VertexBufferActivity:BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return VertexBufferRenderer()
    }
}