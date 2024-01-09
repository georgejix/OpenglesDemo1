package com.example.openglesdemo1.ui.normal.t5

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

/**
 * 缓冲数组
 */
class VertexArrayActivity : BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return VertexArrayRender()
    }
}