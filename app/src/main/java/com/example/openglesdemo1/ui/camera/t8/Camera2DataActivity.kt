package com.example.openglesdemo1.ui.camera.t8

import android.annotation.SuppressLint
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2

class Camera2DataActivity : BaseActivity2() {
    private val TAG = "Camera2DataActivity"
    private val mCameraManage by lazy { getSystemService(CAMERA_SERVICE) as CameraManager }
    private var mCamera: CameraDevice? = null
    private var mSession: CameraCaptureSession? = null

    private val mTextureView: TextureView by lazy { findViewById<TextureView>(R.id.textureView) }
    private var mBackCameraId: String? = null
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mSurface: Surface? = null
    private var mImageReaderSurface: Surface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_camera2_data)
        mTextureView.surfaceTextureListener =
            object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(
                    surface: SurfaceTexture, width: Int, height: Int
                ) {
                    mSurfaceTexture = surface
                    mSurface = Surface(surface)
                    initCamera2()
                }

                override fun onSurfaceTextureSizeChanged(
                    surface: SurfaceTexture, width: Int, height: Int
                ) {
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                    return true
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun initCamera2() {
        mBackCameraId = mCameraManage.cameraIdList.find {
            mCameraManage.getCameraCharacteristics(it)
                .get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
        }
        mBackCameraId?.let { cameraId ->
            mCameraManage.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    mCamera = camera
                    val map = mCameraManage.getCameraCharacteristics(cameraId)
                        .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    map?.getOutputSizes(SurfaceTexture::class.java)?.forEach { size ->
                        if (720 == size.height) {
                            //resize layout
                            Log.d(TAG, "${size.width}  ${size.height}")
                            //previewsize
                            mSurfaceTexture?.setDefaultBufferSize(size.width, size.height)
                            if (mTextureView.layoutParams is ConstraintLayout.LayoutParams) {
                                val param =
                                    mTextureView.layoutParams as ConstraintLayout.LayoutParams
                                param.dimensionRatio = "w,${size.width}:${size.height}"
                                mTextureView.layoutParams = param
                            }
                            createImageReader(size)
                            createSession()
                        }
                    }
                }

                override fun onDisconnected(camera: CameraDevice) {
                    mCamera = null
                }

                override fun onError(camera: CameraDevice, error: Int) {
                }

            }, null)
        }
    }

    private fun createImageReader(size: Size) {
        //JPEG/YUV_420_888
        val imageReader =
            ImageReader.newInstance(size.width, size.height, ImageFormat.YUV_420_888, 5)
        imageReader.setOnImageAvailableListener(object : ImageReader.OnImageAvailableListener {
            override fun onImageAvailable(reader: ImageReader?) {
                reader?.acquireNextImage()?.close()
                Log.d(TAG, "camera2 data onImageAvailable")
            }
        }, null)
        mImageReaderSurface = imageReader.surface
    }

    @SuppressLint("MissingPermission")
    private fun createSession() {
        mCamera?.createCaptureSession(
            arrayListOf(mSurface, mImageReaderSurface),
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
        //set fps
        mBackCameraId?.let {
            val fpsList = mCameraManage.getCameraCharacteristics(it)
                .get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES)
            fpsList?.forEach { fps ->
                if ((fps?.upper ?: 30) <= 15) {
                    Log.d(TAG, "fps: ${fps.lower} ${fps.upper}")
                    request?.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fps)
                }
            }
        }
        mSurface?.let { request?.addTarget(it) }
        mImageReaderSurface?.let { request?.addTarget(it) }
        request?.let {
            mSession?.setRepeatingRequest(
                it.build(),
                object : CameraCaptureSession.CaptureCallback() {
                    override fun onCaptureCompleted(
                        session: CameraCaptureSession,
                        request: CaptureRequest,
                        result: TotalCaptureResult
                    ) {
                        super.onCaptureCompleted(session, request, result)
                    }
                }, null
            )
        }
    }
}