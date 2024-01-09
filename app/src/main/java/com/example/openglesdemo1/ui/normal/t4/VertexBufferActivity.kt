package com.example.openglesdemo1.ui.normal.t4

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

/**
 * 缓冲区
 */
class VertexBufferActivity:BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return VertexBufferRender()
    }
}