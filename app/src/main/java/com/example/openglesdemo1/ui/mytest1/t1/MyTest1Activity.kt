package com.example.openglesdemo1.ui.mytest1.t1

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.View
import androidx.core.app.ActivityCompat
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import kotlinx.android.synthetic.main.activity_mytest1.*

class MyTest1Activity : BaseActivity2() {
    private var mInitView = true
    private var mMyTest01Render: MyTest01Render? = null
    private var mHandler: Handler? = null
    private var mHandlerThread: HandlerThread? = null
    private val mCameraManager: CameraManager by lazy { getSystemService(Context.CAMERA_SERVICE) as CameraManager }

    private var mCameraDevice: CameraDevice? = null

    private var mUseBackCamera = true
    private var mFrontCameraId = ""
    private var mBackCameraId = ""
    private var mFrontCameraSizeList: Array<Size>? = null
    private var mBackCameraSizeList: Array<Size>? = null
    private var mSizeIndex = 0
    private var mSize: Size? = null

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
                Log.d(MyTest1Activity.TAG, "MyTest01Render.onOpenCamera")
                if (mFrontCameraId.isEmpty()) {
                    getCameraParams()
                }
                setCameraSize()
            }
        })
        gl_surface.setRenderer(mMyTest01Render)
        tv_change_size.setOnClickListener(this::onClick)
        tv_change_camera.setOnClickListener(this::onClick)
        tv_change_filter.setOnClickListener(this::onClick)
        tv_take_photo.setOnClickListener(this::onClick)
    }

    private fun onClick(view: View) {
        when (view.id) {
            R.id.tv_change_size -> {
                mSizeIndex++
                setCameraSize()
            }
            R.id.tv_change_camera -> {
                mCameraDevice?.close()
                mUseBackCamera = !mUseBackCamera
                mSizeIndex = 0
                setCameraSize()
            }
            R.id.tv_change_filter -> gl_surface.queueEvent { mMyTest01Render?.changeFilter() }
            R.id.tv_take_photo -> mMyTest01Render?.takePhoto()
        }
    }

    private fun getCameraParams() {
        mCameraManager.cameraIdList.forEach { id ->
            val ch = mCameraManager.getCameraCharacteristics(id)
            when (ch.get(CameraCharacteristics.LENS_FACING)) {
                CameraCharacteristics.LENS_FACING_BACK -> {
                    mBackCameraId = id
                    val map = ch.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    mBackCameraSizeList = map?.getOutputSizes(SurfaceTexture::class.java)
                }
                CameraCharacteristics.LENS_FACING_FRONT -> {
                    mFrontCameraId = id
                    val map = ch.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    mFrontCameraSizeList = map?.getOutputSizes(SurfaceTexture::class.java)
                }
            }
        }
    }

    private fun setCameraSize() {
        mCameraDevice?.close()
        mSize = if (mUseBackCamera) mBackCameraSizeList?.get(
            mSizeIndex % (mBackCameraSizeList?.size ?: 1)
        )
        else mFrontCameraSizeList?.get(mSizeIndex % (mFrontCameraSizeList?.size ?: 1))
        post { tv_size.text = "${mSize?.width}x${mSize?.height}" }
        val height = (mSize?.width ?: 0) * gl_surface.width / (mSize?.height ?: 0)
        val p = (gl_surface.height * 1.0 / height).toFloat()
        Log.d(MyTest1Activity.TAG, "heightP = ${p}")
        gl_surface.queueEvent {
            mMyTest01Render?.setMatrix(mUseBackCamera, p)
            openCamera()
        }
    }

    private fun openCamera() {
        Log.d(TAG, "openCamera")
        mCameraDevice?.close()
        gl_surface.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mCameraManager.openCamera(
            if (mUseBackCamera) mBackCameraId else mFrontCameraId,
            object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    mCameraDevice = camera
                    val surfaceT = mMyTest01Render?.getSurfaceTexture()
                    surfaceT ?: return
                    surfaceT.setOnFrameAvailableListener { gl_surface.requestRender() }
                    surfaceT.setDefaultBufferSize(mSize?.width ?: 1080, mSize?.height ?: 1920)
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
            }, mHandler
        )
    }

    fun setFilterStr(str: String) {
        post { tv_filter.text = str }
    }

    private fun post(runnable: Runnable) {
        runOnUiThread(runnable)
    }
}