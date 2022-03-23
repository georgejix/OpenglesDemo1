package com.example.openglesdemo1.ui.t9

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.core.app.ActivityCompat

class SurfaceCameraActivity : Activity() {
    private lateinit var mGLSurfaceView: GLSurfaceView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyPermission();
    }

    private fun applyPermission() {
        if (PackageManager.PERMISSION_GRANTED !=
            ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        } else {
            setupView()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (0 == requestCode) {
            if (PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            ) {
                setupView()
            }
        }
    }

    private fun setupView() {
        mGLSurfaceView = GLSurfaceView(this)
        mGLSurfaceView.setEGLContextClientVersion(3)
        mGLSurfaceView.setRenderer(SurfaceCameraRender(mGLSurfaceView))
        setContentView(mGLSurfaceView)
    }
}