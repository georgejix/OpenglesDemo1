package com.example.openglesdemo1.ui.normal.t15

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

class FboIboActivity : BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return FboIboRenderer(this)
    }
}