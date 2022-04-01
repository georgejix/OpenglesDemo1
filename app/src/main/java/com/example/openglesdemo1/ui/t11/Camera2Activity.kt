package com.example.openglesdemo1.ui.t11

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.media.Image
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import com.example.openglesdemo1.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_camera2.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Camera2Activity : BaseActivity2() {
    private var mCameraThread: HandlerThread? = null
    private var mCameraHandler: Handler? = null
    private var mPreviewSize: Size? = null
    private var mCaptureSize: Size? = null
    private var mCameraId: String? = null
    private var mCameraDevice: CameraDevice? = null
    private val mTextureView: TextureView? = null
    private var mImageReader: ImageReader? = null
    private var mCaptureRequestBuilder: CaptureRequest.Builder? = null
    private var mCaptureRequest: CaptureRequest? = null
    private var mCameraCaptureSession: CameraCaptureSession? = null
    private val ORIENTATION = SparseIntArray()
    private var mWidth: Int = 0
    private var mHeight: Int = 0

    init {
        ORIENTATION.append(Surface.ROTATION_0, 90)
        ORIENTATION.append(Surface.ROTATION_90, 0)
        ORIENTATION.append(Surface.ROTATION_180, 270)
        ORIENTATION.append(Surface.ROTATION_270, 180)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)
        changeStatusBars(false, btn_take_photo)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_take_photo -> {
                ToastUtil.showToast("take photo")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        textureView.surfaceTextureListener ?: let {
            textureView.surfaceTextureListener = mTextureListener
        }
    }

    override fun getPermissions(get: Boolean, requestCode: Int) {
        super.getPermissions(get, requestCode)
        if (get) {
            startCameraThread()
            setupCamera(mWidth, mHeight)
            openCamera()
        }
    }

    private fun startCameraThread() {
        mCameraThread ?: let {
            mCameraThread = HandlerThread("CameraThread")
            mCameraThread?.start()
            mCameraHandler = Handler(mCameraThread!!.looper)
        }
    }

    private val mTextureListener: SurfaceTextureListener = object : SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            mWidth = width
            mHeight = height
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 0)
        }

        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture,
            width: Int,
            height: Int
        ) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
    }

    private fun setupCamera(width: Int, height: Int) {
        //获取摄像头的管理者CameraManager
        val manager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            //遍历所有摄像头
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                //此处默认打开后置摄像头
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) continue
                //获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
                val map =
                    characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                //根据TextureView的尺寸设置预览尺寸
                mPreviewSize =
                    getOptimalSize(map.getOutputSizes(SurfaceTexture::class.java), width, height)
                //获取相机支持的最大拍照尺寸
                mCaptureSize = Collections.max(
                    Arrays.asList(*map.getOutputSizes(ImageFormat.JPEG))
                ) { lhs, rhs -> java.lang.Long.signum((lhs.width * lhs.height - rhs.height * rhs.width).toLong()) }
                //此ImageReader用于拍照所需
                setupImageReader()
                mCameraId = cameraId
                break
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    //选择sizeMap中大于并且最接近width和height的size
    private fun getOptimalSize(sizeMap: Array<Size>, width: Int, height: Int): Size? {
        val sizeList: MutableList<Size> = ArrayList()
        for (option in sizeMap) {
            if (width > height) {
                if (option.width > width && option.height > height) {
                    sizeList.add(option)
                }
            } else {
                if (option.width > height && option.height > width) {
                    sizeList.add(option)
                }
            }
        }
        return if (sizeList.size > 0) {
            Collections.min(
                sizeList
            ) { lhs, rhs -> java.lang.Long.signum((lhs.width * lhs.height - rhs.width * rhs.height).toLong()) }
        } else sizeMap[0]
    }


    private fun openCamera() {
        val manager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mCameraId?.apply { manager.openCamera(this, mStateCallback, mCameraHandler) }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private val mStateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            mCameraDevice = camera
            startPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
            mCameraDevice = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
            mCameraDevice = null
        }
    }

    private fun startPreview() {
        val mSurfaceTexture: SurfaceTexture? = textureView.getSurfaceTexture()
        mSurfaceTexture?.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)
        val previewSurface = Surface(mSurfaceTexture)
        try {
            mCaptureRequestBuilder =
                mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mCaptureRequestBuilder?.addTarget(previewSurface)
            mCameraDevice?.createCaptureSession(
                Arrays.asList(
                    previewSurface,
                    mImageReader?.getSurface()
                ), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        try {
                            mCaptureRequest = mCaptureRequestBuilder?.build()
                            mCameraCaptureSession = session
                            mCameraCaptureSession?.setRepeatingRequest(
                                mCaptureRequest!!,
                                null,
                                mCameraHandler
                            )
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {}
                }, mCameraHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun takePicture(view: View?) {
        lockFocus()
    }

    private fun lockFocus() {
        try {
            mCaptureRequestBuilder?.set<Int>(
                CaptureRequest.CONTROL_AF_TRIGGER,
                CameraMetadata.CONTROL_AF_TRIGGER_START
            )
            mCameraCaptureSession?.capture(
                mCaptureRequestBuilder?.build()!!,
                mCaptureCallback,
                mCameraHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private val mCaptureCallback: CaptureCallback = object : CaptureCallback() {
        override fun onCaptureProgressed(
            session: CameraCaptureSession,
            request: CaptureRequest,
            partialResult: CaptureResult
        ) {
        }

        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            capture()
        }
    }

    private fun capture() {
        try {
            val mCaptureBuilder: CaptureRequest.Builder? =
                mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            val rotation = windowManager.defaultDisplay.rotation
            mCaptureBuilder?.addTarget(mImageReader?.getSurface()!!)
            mCaptureBuilder?.set(
                CaptureRequest.JPEG_ORIENTATION,
                ORIENTATION.get(rotation)
            )
            val CaptureCallback: CaptureCallback = object : CaptureCallback() {
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    Toast.makeText(applicationContext, "Image Saved!", Toast.LENGTH_SHORT).show()
                    unLockFocus()
                }
            }
            mCameraCaptureSession?.stopRepeating()
            mCameraCaptureSession?.capture(mCaptureBuilder?.build()!!, CaptureCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun unLockFocus() {
        try {
            mCaptureRequestBuilder?.set<Int>(
                CaptureRequest.CONTROL_AF_TRIGGER,
                CameraMetadata.CONTROL_AF_TRIGGER_CANCEL
            )
            //mCameraCaptureSession.capture(mCaptureRequestBuilder.build(), null, mCameraHandler);
            mCameraCaptureSession?.setRepeatingRequest(mCaptureRequest!!, null, mCameraHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        mCameraCaptureSession?.close()
        mCameraCaptureSession = null
        mCameraDevice?.close()
        mCameraDevice = null
        mImageReader?.close()
        mImageReader = null
    }

    private fun setupImageReader() {
        //2代表ImageReader中最多可以获取两帧图像流
        mImageReader = ImageReader.newInstance(
            mCaptureSize!!.width, mCaptureSize!!.height,
            ImageFormat.JPEG, 2
        )
        mImageReader?.setOnImageAvailableListener(OnImageAvailableListener { reader ->
            mCameraHandler!!.post(
                imageSaver(reader.acquireNextImage())
            )
        }, mCameraHandler)
    }

    class imageSaver(private val mImage: Image) : Runnable {
        override fun run() {
            val buffer = mImage.planes[0].buffer
            val data = ByteArray(buffer.remaining())
            buffer[data]
            val path = Environment.getExternalStorageDirectory().toString() + "/DCIM/CameraV2/"
            val mImageFile = File(path)
            if (!mImageFile.exists()) {
                mImageFile.mkdir()
            }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val fileName = path + "IMG_" + timeStamp + ".jpg"
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(fileName)
                fos.write(data, 0, data.size)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (fos != null) {
                    try {
                        fos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}