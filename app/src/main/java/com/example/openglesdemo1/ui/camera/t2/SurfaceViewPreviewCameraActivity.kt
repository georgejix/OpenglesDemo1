package com.example.openglesdemo1.ui.camera.t2

import android.annotation.SuppressLint
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity2

class SurfaceViewPreviewCameraActivity : BaseActivity2() {

    private val TAG = "SurfaceViewPreviewCamera"
    private var mSurfaceHolder: SurfaceHolder? = null
    private val mCameraManage by lazy { getSystemService(CAMERA_SERVICE) as CameraManager }
    private var mCamera: CameraDevice? = null
    private var mSession: CameraCaptureSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val surfaceView = SurfaceView(this)
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                mSurfaceHolder = holder
                initCamera2()
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

    @SuppressLint("MissingPermission")
    private fun initCamera2() {
        val backCameraId = mCameraManage.cameraIdList.find {
            mCameraManage.getCameraCharacteristics(it)
                .get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
        }
        backCameraId?.let { cameraId ->
            mCameraManage.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    mCamera = camera
                    createSession()
                }

                override fun onDisconnected(camera: CameraDevice) {
                    mCamera = null
                }

                override fun onError(camera: CameraDevice, error: Int) {
                }

            }, null)
        }
    }

    @SuppressLint("MissingPermission")
    private fun createSession() {
        mCamera?.createCaptureSession(
            arrayListOf(mSurfaceHolder?.surface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    mSession = session
                    startPreview()
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    mSession = null
                }
            },
            null
        )
    }

    private fun startPreview() {
        val request = mCamera?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        mSurfaceHolder?.surface?.let { request?.addTarget(it) }
        request?.let {
            mSession?.setRepeatingRequest(
                it.build(),
                object : CameraCaptureSession.CaptureCallback() {}, null
            )
        }
    }
}