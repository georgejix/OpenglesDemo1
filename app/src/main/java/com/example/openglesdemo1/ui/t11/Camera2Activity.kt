package com.example.openglesdemo1.ui.t11

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.*
import android.media.ExifInterface
import android.media.ImageReader
import android.os.*
import android.provider.MediaStore
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
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque

/**
 * https://www.jianshu.com/u/1bda0082f088
 * camera2
 */
class Camera2Activity : BaseActivity2() {
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
    private val mSaveImageExecutor: Executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)
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
                mCapBuilder!!.build(),
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
                    val width = it.width
                    val height = it.height
                    mSaveImageExecutor.execute {
                        val date = System.currentTimeMillis()
                        val title = "IMG_${dateFormat.format(date)}"// e.g. IMG_20190211100833786
                        val displayName = "$title.jpeg"// e.g. IMG_20190211100833786.jpeg
                        val path =
                            "$cameraDir/$displayName"// e.g. /sdcard/DCIM/Camera/IMG_20190211100833786.jpeg
                        val orientation = captureResult[CaptureResult.JPEG_ORIENTATION]
                        val location = captureResult[CaptureResult.JPEG_GPS_LOCATION]
                        val longitude = location?.longitude ?: 0.0
                        val latitude = location?.latitude ?: 0.0
                        println(path)

                        // Write the jpeg data into the specified file.
                        File(path).writeBytes(jpegByteArray)

                        // Insert the image information into the media store.
                        val values = ContentValues()
                        values.put(MediaStore.Images.ImageColumns.TITLE, title)
                        values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, displayName)
                        values.put(MediaStore.Images.ImageColumns.DATA, path)
                        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, date)
                        values.put(MediaStore.Images.ImageColumns.WIDTH, width)
                        values.put(MediaStore.Images.ImageColumns.HEIGHT, height)
                        values.put(MediaStore.Images.ImageColumns.ORIENTATION, orientation)
                        values.put(MediaStore.Images.ImageColumns.LONGITUDE, longitude)
                        values.put(MediaStore.Images.ImageColumns.LATITUDE, latitude)
                        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                        // Refresh the thumbnail of image.
                        val thumbnail = getThumbnail(path)
                        if (thumbnail != null) {
                            runOnUiThread {
                                thumbnail_view.setImageBitmap(thumbnail)
                                thumbnail_view.scaleX = 0.8F
                                thumbnail_view.scaleY = 0.8F
                                thumbnail_view.animate().setDuration(50).scaleX(1.0F).scaleY(1.0F)
                                    .start()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getThumbnail(jpegPath: String): Bitmap? {
        val exifInterface = ExifInterface(jpegPath)
        val orientationFlag = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        val orientation = when (orientationFlag) {
            ExifInterface.ORIENTATION_NORMAL -> 0.0F
            ExifInterface.ORIENTATION_ROTATE_90 -> 90.0F
            ExifInterface.ORIENTATION_ROTATE_180 -> 180.0F
            ExifInterface.ORIENTATION_ROTATE_270 -> 270.0F
            else -> 0.0F
        }

        var thumbnail = if (exifInterface.hasThumbnail() && Build.VERSION.SDK_INT >= 26) {
            exifInterface.thumbnailBitmap
        } else {
            val options = BitmapFactory.Options()
            options.inSampleSize = 16
            BitmapFactory.decodeFile(jpegPath, options)
        }

        if (orientation != 0.0F && thumbnail != null) {
            val matrix = Matrix()
            matrix.setRotate(orientation)
            thumbnail = Bitmap.createBitmap(
                thumbnail,
                0,
                0,
                thumbnail.width,
                thumbnail.height,
                matrix,
                true
            )
        }

        return thumbnail
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