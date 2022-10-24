package com.example.openglesdemo1.ui.stu1.t7

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