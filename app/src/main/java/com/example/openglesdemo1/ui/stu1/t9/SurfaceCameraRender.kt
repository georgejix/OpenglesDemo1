package com.example.openglesdemo1.ui.stu1.t9

import android.app.Activity
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.view.Surface
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SurfaceCameraRender(val glSurfaceView: GLSurfaceView) : GLSurfaceView.Renderer {
    private var mVertexBuffer: FloatBuffer
    private var mTexVertexBuffer: FloatBuffer
    private var mVertexIndexBuffer: ShortBuffer
    private var mProgram = 0
    private var mTextureId = 0

    /**
     * 顶点坐标
     * (x,y,z)
     */
    private val mVertexData = floatArrayOf(
        0f, 0f, 0f,     //顶点坐标V0
        1f, 1f, 0f,     //顶点坐标V1
        -1f, 1f, 0f,    //顶点坐标V2
        -1f, -1f, 0f,   //顶点坐标V3
        1f, -1f, 0f     //顶点坐标V4
    )

    /**
     * 纹理坐标
     * (s,t)
     */
    private val mTexVertexData = floatArrayOf(
        0.5f, 0.5f, //纹理坐标V0
        1f, 1f,     //纹理坐标V1
        0f, 1f,     //纹理坐标V2
        0f, 0.0f,   //纹理坐标V3
        1f, 0.0f    //纹理坐标V4
    )

    /**
     * 索引
     */
    private val mVertexIndexData = shortArrayOf(
        /**
         * 1, 3, 2,
         * 1, 3, 4,
         */
        0, 1, 2,  //V0,V1,V2 三个顶点组成一个三角形
        0, 2, 3,  //V0,V2,V3 三个顶点组成一个三角形
        0, 3, 4,  //V0,V3,V4 三个顶点组成一个三角形
        0, 4, 1   //V0,V4,V1 三个顶点组成一个三角形
    )

    private var mMatrix = FloatArray(16)
    private var mCameraId = 0
    private var mCamera: Camera? = null
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mMatrixLocation = 0
    private var mTextureSamplerLocation = 0

    init {
        mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT
        mCamera = Camera.open(mCameraId)
        setCameraDisplayOrientation(mCameraId, mCamera)

        mVertexBuffer = ByteBuffer.allocateDirect(mVertexData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexBuffer.put(mVertexData).position(0)
        mTexVertexBuffer = ByteBuffer.allocateDirect(mTexVertexData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTexVertexBuffer.put(mTexVertexData).position(0)
        mVertexIndexBuffer = ByteBuffer.allocateDirect(mVertexIndexData.size * 4)
            .order(ByteOrder.nativeOrder()).asShortBuffer()
        mVertexIndexBuffer.put(mVertexIndexData).position(0)
    }

    private fun setCameraDisplayOrientation(cameraId: Int, camera: Camera?) {
        val activity = glSurfaceView.context as Activity
        val cameraInfo = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, cameraInfo)
        val degrees = when (activity.windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }
        var result = 0
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + degrees) % 360
            result = (360 - result) % 360
        } else {
            result = (cameraInfo.orientation - degrees + 360) % 360
        }
        camera?.setDisplayOrientation(result)
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

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f)
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
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 0, mTexVertexBuffer)
        // 绘制
        GLES30.glDrawElements(
            GLES30.GL_TRIANGLES,
            mVertexIndexData.size,
            GLES30.GL_UNSIGNED_SHORT,
            mVertexIndexBuffer
        )
    }

    private fun loadSurfaceTexture(textureId: Int): Boolean {
        //根据纹理ID创建SurfaceTexture
        mSurfaceTexture = SurfaceTexture(textureId)
        mSurfaceTexture?.setOnFrameAvailableListener {
            glSurfaceView.requestRender()
        }
        //设置SurfaceTexture作为相机预览输出
        mCamera?.setPreviewTexture(mSurfaceTexture)
        mCamera?.startPreview()
        return true
    }

    fun release() {
        mCamera?.release()
        mCamera = null
    }
}