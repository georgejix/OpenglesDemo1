package com.example.openglesdemo1.ui.t12

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class RecordByCameraRender(val mGLSurfaceView: GLSurfaceView) :
    GLSurfaceView.Renderer {

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
    private var mCameraId = -1
    private var mCamera: Camera? = null
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mMatrixLocation = 0
    private var mTextureSamplerLocation = 0

    init {
        mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT
        mCamera = Camera.open(mCameraId)
        mCamera?.setDisplayOrientation(90)

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
        //将此纹理单元床位片段着色器的uTextureSampler外部纹理采样器
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

    fun start() {

    }

    fun stop() {

    }

    fun release() {

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
        //设置SurfaceTexture作为相机预览输出
        mCamera?.setPreviewTexture(mSurfaceTexture)
        mCamera?.startPreview()
        return true
    }
}