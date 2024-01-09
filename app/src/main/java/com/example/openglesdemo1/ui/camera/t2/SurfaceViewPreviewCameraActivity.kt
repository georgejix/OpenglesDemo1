package com.example.openglesdemo1.ui.camera.t2

import android.hardware.Camera
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity2

class SurfaceViewPreviewCameraActivity : BaseActivity2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val surfaceView = SurfaceView(this)
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                val camera = Camera.open(0)
                camera.setPreviewDisplay(holder)
                camera.setDisplayOrientation(90)
                camera.startPreview()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder, format: Int, width: Int, height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }

        })
        setContentView(surfaceView)
    }
}