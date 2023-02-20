package com.example.openglesdemo1.ui.stu2.t3

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Range
import android.util.Size
import android.view.Surface
import androidx.core.app.ActivityCompat
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import java.lang.Math.abs
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GlPreviewCameraActivity2Render(val mGLSurfaceView: GLSurfaceView, var mListener: Listener) :
    GLSurfaceView.Renderer {

    interface Listener {
        fun onSurfaceCreated()
    }

    private var mVertexBuffer: FloatBuffer
    private var mTextureBuffer: FloatBuffer
    private var mIndexBuffer: ShortBuffer
    private var mProgram = 0
    private var mTextureId = 0
    private val mVertexData = floatArrayOf(
        0f, 0f, 0f,
        1f, 1f, 0f,
        -1f, 1f, 0f,
        -1f, -1f, 0f,
        1f, -1f, 0f
    )
    private val mTextureData = floatArrayOf(
        0.5f, 0.5f, //纹理坐标V0
        1f, 1f,     //纹理坐标V1
        0f, 1f,     //纹理坐标V2
        0f, 0.0f,   //纹理坐标V3
        1f, 0.0f    //纹理坐标V4
    )
    private val mIndexData = shortArrayOf(
        1, 3, 2,
        1, 3, 4
    )
    private var mMatrix = FloatArray(16)
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mMatrixLocation = 0
    private var mTextureSamplerLocation = 0

    private var mCameraId: String? = null
    private var mCamera: CameraDevice? = null
    private val mCameraManager: CameraManager by
    lazy { mGLSurfaceView.context.getSystemService(CameraManager::class.java) }
    private var mSecondHandler: Handler? = null
    private var mCameraInfo: CameraCharacteristics? = null
    public var mSize: Size? = null
    private val mSupportWith = 1080;
    private var mFps: Range<Int>? = null
    private val mSupportFps = 24;
    private var mCameraSession: CameraCaptureSession? = null
    private var mCaptureBuilder: CaptureRequest.Builder? = null
    private var mSurface: Surface? = null
    private var mHandlerThread = HandlerThread("second")
    private var mImageReader: ImageReader? = null
    private var mImageSurface: Surface? = null
    private var mFrameCount = 0

    init {
        for (id in mCameraManager.cameraIdList) {
            var cameraInfo = mCameraManager.getCameraCharacteristics(id)
            if (CameraCharacteristics.LENS_FACING_FRONT ==
                cameraInfo[CameraCharacteristics.LENS_FACING]
            ) {
                mCameraId = id
                mCameraInfo = cameraInfo
                getCameraParams()
                openCamera()
                break
            }
        }

        mVertexBuffer = ByteBuffer.allocateDirect(mVertexData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexBuffer.put(mVertexData).position(0)
        mTextureBuffer = ByteBuffer.allocateDirect(mTextureData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTextureBuffer.put(mTextureData).position(0)
        mIndexBuffer = ByteBuffer.allocateDirect(mIndexData.size * 4)
            .order(ByteOrder.nativeOrder()).asShortBuffer()
        mIndexBuffer.put(mIndexData).position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(0f, 0f, 0f, 0f)
        val shaderId =
            ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_camera_shader))
        val fragShader =
            ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_camera_shader))
        mProgram = ShaderUtils.linkProgram(shaderId, fragShader)
        mMatrixLocation = GLES30.glGetUniformLocation(mProgram, "uTextureMatrix")
        mTextureSamplerLocation = GLES30.glGetUniformLocation(mProgram, "yuvTexSampler")
        //加载纹理
        mTextureId = loadTexture()
        //加载SurfaceTexture
        loadSurfaceTexture(mTextureId)
        mListener.onSurfaceCreated()
        /**
         * 刷新方式:注意必须在setRenderer 后面设置
         * RENDERMODE_WHEN_DIRTY 手动刷新，調用requestRender()回调一次渲染器的onDraw方法
         * RENDERMODE_CONTINUOUSLY 自动刷新，大概16ms自動回調一次渲染器的onDraw方法
         */
        mGLSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glUseProgram(mProgram)
        //更新纹理图像
        mSurfaceTexture?.updateTexImage()
        mSurfaceTexture?.getTransformMatrix(mMatrix)
        //激活纹理单元0
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        //绑定外部纹理到纹理单元0
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId)
        //将此纹理单元传给片段着色器的uTextureSampler外部纹理采样器
        GLES30.glUniform1i(mTextureSamplerLocation, 0)
        //将纹理矩阵传给片段着色器
        GLES30.glUniformMatrix4fv(mMatrixLocation, 1, false, mMatrix, 0)

        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, mVertexBuffer)
        GLES30.glEnableVertexAttribArray(1)
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 0, mTextureBuffer)
        // 绘制
        GLES30.glDrawElements(
            GLES30.GL_TRIANGLES,
            mIndexData.size,
            GLES30.GL_UNSIGNED_SHORT,
            mIndexBuffer
        )
    }

    private fun loadTexture(): Int {
        var textureId = IntArray(1)
        //创建一个纹理
        GLES30.glGenTextures(1, textureId, 0)
        //绑定到外部纹理上
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId[0])

        //设置纹理过滤参数
        GLES30.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_NEAREST
        )
        GLES30.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_MAG_FILTER,
            GLES30.GL_LINEAR
        )
        GLES30.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_WRAP_S,
            GLES30.GL_CLAMP_TO_EDGE
        )
        GLES30.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_WRAP_T,
            GLES30.GL_CLAMP_TO_EDGE
        )
        //解除纹理绑定
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
        return textureId[0]
    }

    private fun loadSurfaceTexture(textureId: Int): Boolean {
        //根据纹理ID创建SurfaceTexture
        mSurfaceTexture = SurfaceTexture(textureId)
        mSurfaceTexture?.setOnFrameAvailableListener {
            mGLSurfaceView.requestRender()
        }
        return true
    }

    fun start() {
        mCameraSession?.let {
            setCaptureRequest()
        } ?: initCameraSession()
    }

    fun stop() {
        mCameraSession?.stopRepeating()
    }

    fun release() {
        mHandlerThread.quitSafely()
        mCamera?.close()
        mCamera = null
    }

    private fun openCamera() {
        mHandlerThread.start()
        mSecondHandler = Handler(mHandlerThread.looper)
        if (ActivityCompat.checkSelfPermission(
                mGLSurfaceView.context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mCameraManager.openCamera(
                mCameraId!!,
                object : CameraDevice.StateCallback() {
                    override fun onOpened(camera: CameraDevice) {
                        mCamera = camera
                    }

                    override fun onDisconnected(camera: CameraDevice) {
                        camera.close()
                    }

                    override fun onError(camera: CameraDevice, error: Int) {
                        camera.close()
                    }
                },
                mSecondHandler
            )
        }
    }

    private fun getCameraParams() {
        mCameraInfo?.apply {
            val map = get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            map?.getOutputSizes(SurfaceTexture::class.java)?.apply {
                for (size in this) {
                    mSize?.apply {
                        if (abs(size.height - mSupportWith) < abs(height - mSupportWith)) {
                            mSize = size
                        }
                    } ?: let {
                        mSize = size
                    }
                }
            }

            val fpsList = get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES)
            fpsList?.forEach { fps ->
                if (fps.lower == fps.upper) {
                    mFps?.apply {
                        if (abs(fps.upper - mSupportFps) < abs(upper - mSupportFps)) {
                            mFps = fps
                        }
                    } ?: let {
                        mFps = fps
                    }
                }
            }
        }
    }

    private fun initCameraSession() {
        mSize?.apply {
            mSurfaceTexture?.setDefaultBufferSize(width, height)
        }
        mSurfaceTexture?.let {
            mSurface = Surface(mSurfaceTexture)
        }
        //imagereader
        mImageReader =
            ImageReader.newInstance(mSize?.width!!, mSize?.height!!, ImageFormat.YUV_420_888, 5)
        mImageReader?.setOnImageAvailableListener(object : ImageReader.OnImageAvailableListener {
            override fun onImageAvailable(reader: ImageReader?) {
                val image = reader?.acquireLatestImage()
                Log.d(
                    "test",
                    "planes.size=${image?.planes?.size},width=${image?.width},height=${image?.height},frames=${mFrameCount++}"
                )
                image?.close()
            }
        }, mSecondHandler)
        mImageSurface = mImageReader?.surface

        mCamera?.apply {
            createCaptureSession(
                listOf(mSurface, mImageSurface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        mCameraSession = session
                        setCaptureRequest()
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                    }
                },
                mSecondHandler
            )
        }
    }

    private fun setCaptureRequest() {
        mCaptureBuilder ?: let {
            mCaptureBuilder = mCamera?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mFps?.apply {
                mCaptureBuilder?.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, this)
            }
            mCaptureBuilder?.addTarget(mSurface!!)
            mCaptureBuilder?.addTarget(mImageSurface!!)
        }
        mCameraSession?.setRepeatingRequest(
            mCaptureBuilder!!.build(),
            object : CameraCaptureSession.CaptureCallback() {
            },
            mSecondHandler
        )
    }
}