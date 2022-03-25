package com.example.openglesdemo1.ui.t10

import android.graphics.SurfaceTexture

interface ITextureRenderer {
    fun onSurfaceCreated()

    fun onSurfaceChanged(width: Int, height: Int)

    fun onDrawFrame(surfaceTexture: SurfaceTexture?)
}