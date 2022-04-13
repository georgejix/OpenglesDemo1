package com.example.openglesdemo1.ui.t7

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

/**
 * 索引画立方体
 */
class SquareActivity:BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return SquareRender()
    }
}