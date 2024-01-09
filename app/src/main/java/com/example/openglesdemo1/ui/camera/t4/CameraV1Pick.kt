package com.example.openglesdemo1.ui.camera.t4

import android.app.Activity
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.util.Log
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import com.example.openglesdemo1.utils.TextureUtils

class CameraV1Pick : SurfaceTextureListener {
    private val TAG = "CameraV1Pick"
    private var mTextureView: TextureView? = null

    private var mCameraId = 0

    private var mCamera: ICamera? = null

    private var mTextureEglHelper: TextureEGLHelper? = null
    fun bindTextureView(textureView: TextureView?) {
        mTextureView = textureView
        mTextureEglHelper = TextureEGLHelper()
        mTextureView?.surfaceTextureListener = this
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        //加载OES纹理ID
        val textureId: Int = TextureUtils.loadOESTexture()
        //初始化操作
        mTextureEglHelper?.initEgl(mTextureView!!, textureId)
        //自定义的SurfaceTexture
        val surfaceTexture = mTextureEglHelper?.loadOESTexture()
        //前置摄像头
        mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT
        mCamera = CameraV1((mTextureView?.context as Activity))
        if (true == mCamera?.openCamera(mCameraId)) {
            mCamera?.setPreviewTexture(surfaceTexture)
            mCamera?.enablePreview(true)
        } else {
            Log.e(TAG, "openCamera failed")
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        mTextureEglHelper?.onSurfaceChanged(width, height)

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        mCamera?.let {
            mCamera?.enablePreview(false)
            mCamera?.closeCamera()
            mCamera = null
        }
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    }

    fun onDestroy() {
        mTextureEglHelper?.onDestroy()
    }
}