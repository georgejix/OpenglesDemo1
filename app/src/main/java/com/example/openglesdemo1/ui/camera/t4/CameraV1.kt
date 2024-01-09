package com.example.openglesdemo1.ui.camera.t4

import android.app.Activity
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.view.Surface
import java.io.IOException

class CameraV1(val mActivity: Activity) : ICamera {
    private var mCameraId = 0

    private var mCamera: Camera? = null

    override fun openCamera(cameraId: Int): Boolean {
        try {
            mCameraId = cameraId
            mCamera = Camera.open(mCameraId)
            setCameraDisplayOrientation(mActivity, mCameraId, mCamera!!)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun enablePreview(enable: Boolean) {
        mCamera?.let {
            if (enable) {
                mCamera!!.startPreview()
            } else {
                mCamera!!.stopPreview()
            }
        }
    }

    override fun setPreviewTexture(surfaceTexture: SurfaceTexture?) {
        mCamera?.let {
            try {
                mCamera!!.setPreviewTexture(surfaceTexture)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun closeCamera() {
        mCamera?.let {
            mCamera!!.release()
            mCamera = null
        }
    }

    private fun setCameraDisplayOrientation(activity: Activity, cameraId: Int, camera: Camera) {
        val info = CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        val rotation = activity.windowManager.defaultDisplay
            .rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        var result: Int
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360 // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360
        }
        camera.setDisplayOrientation(result)
    }
}