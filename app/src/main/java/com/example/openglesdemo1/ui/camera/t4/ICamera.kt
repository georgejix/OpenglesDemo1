package com.example.openglesdemo1.ui.camera.t4

import android.graphics.SurfaceTexture

interface ICamera {
    fun openCamera(cameraId: Int): Boolean

    fun enablePreview(enable: Boolean)

    fun setPreviewTexture(surfaceTexture: SurfaceTexture?)

    fun closeCamera()
}