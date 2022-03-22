package com.example.openglesdemo1.ui.t8

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

class TextureActivity : BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer = TextureRender()
}