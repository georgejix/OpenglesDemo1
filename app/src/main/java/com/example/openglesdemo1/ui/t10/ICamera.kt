package com.example.openglesdemo1.ui.t10

import android.graphics.SurfaceTexture

interface ICamera {
    fun openCamera(cameraId: Int): Boolean

    fun enablePreview(enable: Boolean)

    fun setPreviewTexture(surfaceTexture: SurfaceTexture?)

    fun closeCamera()
}