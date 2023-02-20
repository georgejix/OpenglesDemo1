package com.example.openglesdemo1.ui.mytest1.t1

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyTest01Render(val mContext: Context, val mListener: Listener?) : GLSurfaceView.Renderer {
    private var mProgramId = 0
    private var mALocation = 0
    private var mTextLocation = 0
    private var mImgLocation = 0
    private var mMatrixLocation = 0
    private val mPoints = floatArrayOf(
        0f, 0f, 0.5f, 0.5f,
        -1f, -1f, 1f, 1f,
        1f, -1f, 1f, 0f,
        1f, 1f, 0f, 0f,
        -1f, 1f, 0f, 1f,
        -1f, -1f, 1f, 1f,
    )
    private val mByteBuffer: FloatBuffer
    private var mCameraTextId = 0
    var mCameraSurfaceTexture: SurfaceTexture? = null
    private var mMatrix = FloatArray(16)

    init {
        mByteBuffer = ByteBuffer.allocateDirect(mPoints.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mByteBuffer.position(0)
        mByteBuffer.put(mPoints)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val vertexId =
            ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.mytest1_vertex))
        val fragmentId =
            ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.mytest1_fragment))
        mProgramId = ShaderUtils.linkProgram(vertexId, fragmentId)
        GLES30.glClearColor(0f, 0f, 0f, 1f)
        mALocation = GLES30.glGetAttribLocation(mProgramId, "aPosition")
        mTextLocation = GLES30.glGetAttribLocation(mProgramId, "aTextCoord")
        mImgLocation = GLES30.glGetUniformLocation(mProgramId, "img")
        mMatrixLocation = GLES30.glGetUniformLocation(mProgramId, "matrix")
        mCameraTextId = loadTexture()
        mCameraSurfaceTexture = SurfaceTexture(mCameraTextId)
        Matrix.orthoM(mMatrix, 0, -1f, 1f, -1f, 1f, -1f, 1f)
        Log.d(MyTest1Activity.TAG, "${mProgramId} ${mCameraTextId}")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        mListener?.onOpenCamera()
    }

    override fun onDrawFrame(gl: GL10?) {
        Log.d(MyTest1Activity.TAG, "onDrawFrame")
        mCameraSurfaceTexture?.updateTexImage()
        //mCameraSurfaceTexture?.getTransformMatrix(mMatrix)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glUseProgram(mProgramId)

        GLES30.glUniformMatrix4fv(mMatrixLocation, 1, false, mMatrix, 0)
        mByteBuffer.position(0)
        GLES30.glVertexAttribPointer(mALocation, 2, GLES30.GL_FLOAT, false, 4 * 4, mByteBuffer)
        GLES30.glEnableVertexAttribArray(mALocation)
        mByteBuffer.position(2)
        GLES30.glVertexAttribPointer(mTextLocation, 2, GLES30.GL_FLOAT, false, 4 * 4, mByteBuffer)
        GLES30.glEnableVertexAttribArray(mTextLocation)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mCameraTextId)
        GLES30.glUniform1i(mImgLocation, 0)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 6)

        GLES30.glDisableVertexAttribArray(mALocation)
        GLES30.glDisableVertexAttribArray(mTextLocation)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
        GLES30.glUseProgram(0)

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

    interface Listener {
        fun onOpenCamera()
    }
}