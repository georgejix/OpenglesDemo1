package com.example.openglesdemo1.ui.mytest1.t1

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import androidx.core.app.ActivityCompat
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import kotlinx.android.synthetic.main.activity_mytest1.*

class MyTest1Activity : BaseActivity2() {
    private var mInitView = true
    private var mMyTest01Render: MyTest01Render? = null
    private var mUseBackCamera = true
    private var mCameraDevice: CameraDevice? = null
    private var mHandler: Handler? = null
    private var mHandlerThread: HandlerThread? = null

    companion object {
        val TAG = "MyTest1Activity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mytest1)
        mInitView = true
    }

    override fun onResume() {
        super.onResume()
        if (mInitView) {
            mHandlerThread = HandlerThread("back")
            mHandlerThread?.start()
            mHandler = Handler(mHandlerThread?.looper!!)
            requestPermission(
                listOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 0
            )
            mInitView = false
        }
    }

    override fun onPause() {
        super.onPause()
        mCameraDevice?.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandlerThread?.quitSafely()
    }

    override fun getPermissions(get: Boolean, requestCode: Int) {
        if (0 == requestCode && get) {
            initView()
        }
    }

    private fun initView() {
        gl_surface.setEGLContextClientVersion(3)
        mMyTest01Render = MyTest01Render(this, object : MyTest01Render.Listener {
            override fun onOpenCamera() {
                openCamera()
            }
        })
        gl_surface.setRenderer(mMyTest01Render)
    }

    private fun openCamera() {
        gl_surface.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        var cameraId = ""
        cameraManager.cameraIdList.forEach { id ->
            val ch = cameraManager.getCameraCharacteristics(id)
            when (ch.get(CameraCharacteristics.LENS_FACING)) {
                CameraCharacteristics.LENS_FACING_BACK -> {
                    if (mUseBackCamera) {
                        cameraId = id
                    }
                }
                CameraCharacteristics.LENS_FACING_FRONT -> {
                    if (!mUseBackCamera) {
                        cameraId = id
                    }
                }

            }
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                mCameraDevice = camera
                val surfaceT = mMyTest01Render?.mCameraSurfaceTexture
                surfaceT?.setOnFrameAvailableListener { gl_surface.requestRender() }
                surfaceT?.setDefaultBufferSize(1080, 1920)
                val surface = Surface(surfaceT)
                camera.createCaptureSession(
                    arrayListOf(surface),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            val builder =
                                mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                            builder?.addTarget(surface)
                            session.setRepeatingRequest(builder?.build()!!, null, mHandler)
                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {
                            session.close()
                        }

                    },
                    mHandler
                )
            }

            override fun onDisconnected(camera: CameraDevice) {
                camera.close()
            }

            override fun onError(camera: CameraDevice, error: Int) {
                camera.close()
            }
        }, mHandler)
    }
}