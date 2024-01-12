package com.example.openglesdemo1.ui.normal.t13

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

class TextureArrayActivity : BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return TextureArrayRenderer(this)
    }
}