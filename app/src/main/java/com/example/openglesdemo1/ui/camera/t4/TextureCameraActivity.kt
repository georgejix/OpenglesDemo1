package com.example.openglesdemo1.ui.camera.t4

import android.Manifest
import android.os.Bundle
import android.view.TextureView
import com.example.openglesdemo1.ui.base.BaseActivity2

/**
 * texture + camera1 + opengl
 */
class TextureCameraActivity : BaseActivity2() {
    private var mTextureView: TextureView? = null

    private var mCameraPick: CameraV1Pick? = null
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
        mTextureView = TextureView(this)
        setContentView(mTextureView)

        mCameraPick = CameraV1Pick()
        mCameraPick?.bindTextureView(mTextureView)
    }

    override fun onDestroy() {
        super.onDestroy()
        mCameraPick?.onDestroy()
    }
}