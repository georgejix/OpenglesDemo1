package com.example.openglesdemo1.ui.t1

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

class TriangleActivity : BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer = HelloTriangleRenderer(this)
}