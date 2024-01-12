package com.example.openglesdemo1.ui.normal.t14

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

class MultiTextureActivity: BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return MultiTextureRenderer(this)
    }
}