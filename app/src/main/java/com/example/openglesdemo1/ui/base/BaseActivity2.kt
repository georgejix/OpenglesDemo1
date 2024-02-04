package com.example.openglesdemo1.ui.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Range
import android.util.Size
import android.view.Surface
import android.view.View
import android.view.WindowInsets
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Collections

open class BaseActivity2 : Activity() {
    private val BASE_TAG = "BaseActivity2"
    var mContext: Context? = null
    private val mHandlerThread by lazy { HandlerThread("back") }
    private val mBackHandler by lazy {
        mHandlerThread.start()
        Handler(mHandlerThread.looper)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
    }

    fun requestPermission(permissions: List<String>, requestCode: Int) {
        var needRequestPermissions = ArrayList<String>()
        permissions.forEach {
            if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(this, it)
            ) {
                needRequestPermissions.add(it)
            }
        }
        if (0 == needRequestPermissions.size) {
            getPermissions(true, requestCode)
        } else {
            ActivityCompat.requestPermissions(
                this, needRequestPermissions.toArray(arrayOf()), requestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        getPermissions(permissions.all {
            PackageManager.PERMISSION_GRANTED ==
                    ContextCompat.checkSelfPermission(this, it)
        }, requestCode)
    }

    open fun getPermissions(get: Boolean, requestCode: Int) {

    }

    //隐藏状态栏
    fun changeStatusBars(isShow: Boolean, view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (isShow) {
                window?.insetsController?.show(WindowInsets.Type.statusBars())
            } else {
                window?.insetsController?.hide(WindowInsets.Type.statusBars())
            }
        } else {
            ViewCompat.getWindowInsetsController(view).let { controller ->
                if (isShow) {
                    controller?.show(WindowInsetsCompat.Type.statusBars())
                } else {
                    controller?.hide(WindowInsetsCompat.Type.statusBars())
                }
            }
        }
    }

    private val mCameraManager by lazy { getSystemService(Context.CAMERA_SERVICE) as CameraManager }
    private var mCamera: CameraDevice? = null
    private var mCameraCaptureSession: CameraCaptureSession? = null
    private var mFps: Range<Int>? = null

    fun closeCamera() {
        mCameraCaptureSession?.stopRepeating()
        mCameraCaptureSession?.close()
        mCamera?.close()
    }

    fun getPreviewSize(): Size? {
        var size: Size? = null
        mCameraManager.cameraIdList.find {
            CameraCharacteristics.LENS_FACING_BACK ==
                    mCameraManager.getCameraCharacteristics(it)
                        .get(CameraCharacteristics.LENS_FACING)
        }?.let { backCameraId ->
            mCameraManager.getCameraCharacteristics(backCameraId)
                .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                ?.getOutputSizes(SurfaceTexture::class.java)
                ?.let {
                    val max = Collections.max(it.toList(),
                        Comparator<Size> { o1, o2 ->
                            (o1?.width ?: 0) * (o2?.height ?: 0) -
                                    (o2?.width ?: 0) * (o2?.height ?: 0)
                        })
                    size = max
                }
        }
        return size
    }

    @SuppressLint("MissingPermission")
    fun openCamera(surface: Surface) {
        mCameraManager.cameraIdList.find {
            CameraCharacteristics.LENS_FACING_BACK ==
                    mCameraManager.getCameraCharacteristics(it)
                        .get(CameraCharacteristics.LENS_FACING)
        }?.let { backCameraId ->
            mCameraManager.getCameraCharacteristics(backCameraId)
                .get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES)
                ?.forEach { range ->
                    mFps ?: let { mFps = range }
                    if (range.lower <= 15 && range.lower > (mFps?.lower ?: 0)) {
                        mFps = range
                    }
                }

            mCameraManager.openCamera(backCameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    mCamera = camera
                    createSession(surface)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    mCamera = null
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    camera.close()
                    mCamera = null
                }
            }, mBackHandler)
        }
    }

    private fun createSession(surface: Surface) {
        /*val config = SessionConfiguration(SessionConfiguration.SESSION_REGULAR,
            listOf(
                OutputConfiguration(surface)
            ), Executors.newSingleThreadExecutor(),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    mCameraCaptureSession = session
                    createRequest(surface)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    mCameraCaptureSession = null
                }
            })
        mCamera?.createCaptureSession(config)*/
        mCamera?.createCaptureSession(
            listOf(surface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    mCameraCaptureSession = session
                    createRequest(surface)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    mCameraCaptureSession = null
                }
            }, null
        )
    }

    private fun createRequest(surface: Surface) {
        mCamera?.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)?.let { builder ->
            builder.addTarget(surface)
            mFps?.let {
                builder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, it)
                Log.d(BASE_TAG, "fps = ${it.upper}-${it.upper}")
            }
            // 设置连续自动对焦
            builder.set(
                CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
            )
            // 设置自动曝光
            builder.set(
                CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
            )
            // 设置自动白平衡
            builder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO)
            mCameraCaptureSession?.setRepeatingRequest(
                builder.build(),
                object : CameraCaptureSession.CaptureCallback() {

                },
                mBackHandler
            )
        }
    }
}