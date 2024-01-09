package com.example.openglesdemo1.ui.normal.t11

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

class TextureAndFilterActivity : BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return TextureAndFilterRender(this)
    }
}