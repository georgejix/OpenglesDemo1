package com.example.openglesdemo1.ui.t6

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

/**
 * 三角形画立方体
 */
class SquareActivity:BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return SquareRender()
    }
}