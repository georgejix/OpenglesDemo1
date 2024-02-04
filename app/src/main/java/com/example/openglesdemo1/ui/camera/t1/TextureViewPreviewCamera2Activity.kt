package com.example.openglesdemo1.ui.camera.t1

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.*
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import com.example.openglesdemo1.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_textureview_preview_camera2.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingDeque

/**
 * https://www.jianshu.com/u/1bda0082f088
 * camera2
 */
class TextureViewPreviewCamera2Activity : BaseActivity2() {
    private val mCameraManager by lazy { getSystemService(CameraManager::class.java) }
    private val mSecondHandler: Handler by lazy { initHandler() }
    private var mCameraDevice: CameraDevice? = null
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mSurface: Surface? = null
    private var mSession: CameraCaptureSession? = null
    private var mCameraId = ""
    private var mCapImageReader: ImageReader? = null
    private var mCapSurface: Surface? = null
    private var mCapBuilder: CaptureRequest.Builder? = null
    private val mCaptureResults: BlockingQueue<CaptureResult> = LinkedBlockingDeque()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_textureview_preview_camera2)
        changeStatusBars(false, btn_take_photo)
        //textureview可用时，申请权限
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
                mSession?.stopRepeating()
                mSession = null
                mCameraDevice?.close()
                mCameraDevice = null
                mCapImageReader?.close()
                mCapImageReader = null
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
                getCapRequest()
            }
        }
    }

    override fun getPermissions(get: Boolean, requestCode: Int) {
        super.getPermissions(get, requestCode)
        if (get) {
            getSupportSize()
        }
    }

    private fun initHandler(): Handler {
        val mHandlerThread = HandlerThread("secondThread")
        mHandlerThread.start()
        return Handler(mHandlerThread.looper)
    }

    //获取支持分辨率
    private fun getSupportSize() {
        mCameraManager.cameraIdList.forEach { cameraId ->
            var cameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId)
            if (cameraCharacteristics.isHardwareLevelSupported(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL)) {
                if (CameraCharacteristics.LENS_FACING_BACK == cameraCharacteristics[CameraCharacteristics.LENS_FACING]) {
                    val map =
                        cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    map?.getOutputSizes(SurfaceTexture::class.java)?.apply {
                        mCameraId = cameraId
                        showSupportSize(this)
                    }
                }
            }
        }
    }

    //显示支持分辨率
    private fun showSupportSize(sizes: Array<Size>) {
        rv_size.adapter = SizeAdapter(sizes.asList(), object : SizeAdapter.Listener {
            override fun onClicked(size: Size) {
                mSession?.stopRepeating()
                mSession = null
                mCameraDevice?.close()
                mCameraDevice = null
                openCamera(size)
            }

        })
    }

    //打开相机
    private fun openCamera(size: Size) {
        println("test $size")
        if (ActivityCompat.checkSelfPermission(
                this@TextureViewPreviewCamera2Activity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val param = textureView.layoutParams as ConstraintLayout.LayoutParams
        param?.dimensionRatio = "w,${size.width}:${size.height}"
        //previewsize
        mSurfaceTexture?.setDefaultBufferSize(size.width, size.height)
        mCameraManager.openCamera(
            mCameraId,
            object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    mCameraDevice = camera
                    createCapImageReader(size)
                    getPreviewSession()
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

    private fun createCapImageReader(size: Size) {
        mCapImageReader = ImageReader.newInstance(size.width, size.height, ImageFormat.JPEG, 3)
        mCapImageReader?.setOnImageAvailableListener(object : ImageReader.OnImageAvailableListener {
            override fun onImageAvailable(reader: ImageReader?) {
                savePic(reader)
            }
        }, mSecondHandler)
        mCapSurface = mCapImageReader?.surface

        mCapBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        mCapBuilder!![CaptureRequest.JPEG_THUMBNAIL_SIZE] = size
        mCapBuilder!![CaptureRequest.JPEG_ORIENTATION] = 90
        mCapBuilder!![CaptureRequest.JPEG_QUALITY] = 100
        mCapBuilder!!.addTarget(mSurface!!)
        mCapBuilder!!.addTarget(mCapSurface!!)
    }

    //获取session对象
    private fun getPreviewSession() {
        mCameraDevice?.createCaptureSession(
            listOf(mSurface, mCapSurface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    mSession = session
                    getPreviewRequest()
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                }
            }, mSecondHandler
        )
    }

    private fun getPreviewRequest() {
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

    private fun getCapRequest() {
        mCapBuilder?.let {
            mSession?.capture(
                it.build(),
                object : CameraCaptureSession.CaptureCallback() {
                    override fun onCaptureCompleted(
                        session: CameraCaptureSession,
                        request: CaptureRequest,
                        result: TotalCaptureResult
                    ) {
                        super.onCaptureCompleted(session, request, result)
                        mCaptureResults.put(result)
                    }
                },
                mSecondHandler
            )
        }
    }

    private fun savePic(reader: ImageReader?) {
        val dateFormat: DateFormat = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault())
        val cameraDir =
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}/Camera"

        reader?.let {
            val image = reader.acquireNextImage()
            val captureResult = mCaptureResults.take()
            if (image != null && captureResult != null) {
                image.let {
                    val jpegByteBuffer =
                        it.planes[0].buffer// Jpeg image data only occupy the planes[0].
                    val jpegByteArray = ByteArray(jpegByteBuffer.remaining())
                    jpegByteBuffer.get(jpegByteArray)
                }
            }
        }
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