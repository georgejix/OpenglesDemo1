package com.example.openglesdemo1.ui.normal.t8

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

/**
 * 纹理
 */
class TextureActivity : BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer = TextureRender()
}