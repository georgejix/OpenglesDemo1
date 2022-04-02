package com.example.openglesdemo1.ui.t11

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import com.example.openglesdemo1.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_camera2.*

/**
 * https://www.jianshu.com/u/1bda0082f088
 */
class Camera2Activity : BaseActivity2() {
    private val mCameraManager by lazy { getSystemService(CameraManager::class.java) }
    private val mSecondHandler: Handler by lazy { initHandler() }
    private var mCameraDevice: CameraDevice? = null
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mSurface: Surface? = null
    private var mSession: CameraCaptureSession? = null
    private var mCameraId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)
        changeStatusBars(false, btn_take_photo)
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                mSurfaceTexture = surface
                mSurface = Surface(mSurfaceTexture)
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 0
                )
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                mCameraDevice?.close()
                mCameraDevice = null
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            }
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_take_photo -> {
                ToastUtil.showToast("take photo")
            }
        }
    }

    override fun getPermissions(get: Boolean, requestCode: Int) {
        super.getPermissions(get, requestCode)
        if (get) {
            startPreview()
        }
    }

    private fun initHandler(): Handler {
        val mHandlerThread = HandlerThread("secondThread")
        mHandlerThread.start()
        return Handler(mHandlerThread.looper)
    }

    private fun startPreview() {
        mCameraManager.cameraIdList.forEach { cameraId ->
            var cameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId)
            if (cameraCharacteristics.isHardwareLevelSupported(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL)) {
                if (CameraCharacteristics.LENS_FACING_BACK == cameraCharacteristics[CameraCharacteristics.LENS_FACING]) {
                    val map =
                        cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    map?.getOutputSizes(SurfaceTexture::class.java)?.apply {
                        mCameraId = cameraId
                        showSize(this)
                    }
                }
            }
        }
        textureView
    }

    private fun showSize(sizes: Array<Size>) {
        rv_size.adapter = SizeAdapter(sizes.asList(), object : SizeAdapter.Listener {
            override fun onClicked(size: Size) {
                mCameraDevice?.close()
                mCameraDevice = null
                open(size)
            }

        })
    }

    private fun open(size: Size) {
        println("test $size")
        if (ActivityCompat.checkSelfPermission(
                this@Camera2Activity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val param = textureView.layoutParams as ConstraintLayout.LayoutParams
        param?.dimensionRatio = "w,${size.width}:${size.height}"
        mSurfaceTexture?.setDefaultBufferSize(size.width, size.height)
        mCameraManager.openCamera(
            mCameraId,
            object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    mCameraDevice = camera
                    openPreview()
                }

                override fun onDisconnected(camera: CameraDevice) {
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    camera.close()
                    mCameraDevice = null
                }
            },
            mSecondHandler
        )
    }

    private fun openPreview() {
        mCameraDevice?.createCaptureSession(
            listOf(mSurface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    mSession = session
                    val builder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                    builder?.addTarget(mSurface!!)
                    val request = builder?.build()
                    mSession?.setRepeatingRequest(
                        request!!,
                        object : CameraCaptureSession.CaptureCallback() {

                        },
                        mSecondHandler
                    )
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                }
            }, mSecondHandler
        )
    }

    /**
     * 判断相机的 Hardware Level 是否大于等于指定的 Level。
     */
    private fun CameraCharacteristics.isHardwareLevelSupported(requiredLevel: Int): Boolean {
        val sortedLevels = intArrayOf(
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY,
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED,
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL,
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3
        )
        val deviceLevel = this[CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL]
        if (requiredLevel == deviceLevel) {
            return true
        }
        for (sortedLevel in sortedLevels) {
            if (requiredLevel == sortedLevel) {
                return true
            } else if (deviceLevel == sortedLevel) {
                return false
            }
        }
        return false
    }

}