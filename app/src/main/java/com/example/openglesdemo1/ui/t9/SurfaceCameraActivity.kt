package com.example.openglesdemo1.ui.t9

import android.Manifest
import android.opengl.GLSurfaceView
import android.os.Bundle
import com.example.openglesdemo1.ui.base.BaseActivity2

/**
 * camera1 + glsurface + opengl
 */
class SurfaceCameraActivity : BaseActivity2() {
    private lateinit var mGLSurfaceView: GLSurfaceView
    private var mRender: SurfaceCameraRender? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission(listOf(Manifest.permission.CAMERA), 0)
    }

    override fun getPermissions(get: Boolean, requestCode: Int) {
        super.getPermissions(get, requestCode)
        if (get) {
            setupView()
        }
    }

    private fun setupView() {
        mGLSurfaceView = GLSurfaceView(this)
        mGLSurfaceView.setEGLContextClientVersion(3)
        mRender = SurfaceCameraRender(mGLSurfaceView)
        mGLSurfaceView.setRenderer(mRender)
        setContentView(mGLSurfaceView)
    }

    override fun onDestroy() {
        super.onDestroy()
        mRender?.release()
    }
}