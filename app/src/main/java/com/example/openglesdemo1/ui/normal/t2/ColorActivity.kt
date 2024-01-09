package com.example.openglesdemo1.ui.normal.t2

import android.graphics.Color
import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

/**
 * 纯色
 */
class ColorActivity : BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer = ColorRender(Color.parseColor("#ff31d77b"))
}