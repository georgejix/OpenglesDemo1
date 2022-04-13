package com.example.openglesdemo1.ui.t1

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

/**
 * 三角形
 */
class TriangleActivity : BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer = HelloTriangleRender(this)
}