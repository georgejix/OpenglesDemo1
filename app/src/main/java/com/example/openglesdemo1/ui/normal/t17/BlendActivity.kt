package com.example.openglesdemo1.ui.normal.t17

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

class BlendActivity : BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return BlendRenderer(this)
    }
}