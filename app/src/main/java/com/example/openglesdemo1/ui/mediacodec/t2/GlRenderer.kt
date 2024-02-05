package com.example.openglesdemo1.ui.mediacodec.t2

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import com.example.openglesdemo1.utils.TextureUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GlRenderer : GLSurfaceView.Renderer {
    private val TAG = "GlRenderer"
    private var mVertexTextureBuffer: FloatBuffer
    private var mIndexBuffer: IntBuffer

    private var mProgramId = 0
    private var mVertexPositionLocation = 0
    private var mTexturePositionLocation = 0
    private var mTextureLocation = 0
    private var mMatrixLocation = 0
    private val mMatrixArray = FloatArray(16)

    private val mVertexTextureArray = floatArrayOf(
        -1f, -1f, 0f, 1f,
        -1f, 1f, 0f, 0f,
        1f, 1f, 1f, 0f,
        1f, -1f, 1f, 1f
    )

    private val mIndexArray = intArrayOf(
        0, 1, 2, 0, 2, 3
    )

    private var mCameraTextureId = -1
    private var mSurfaceTexture: SurfaceTexture? = null

    fun getCameraTextureId() = mCameraTextureId
    fun getSurfaceTexture() = mSurfaceTexture

    init {
        mVertexTextureBuffer =
            ByteBuffer.allocateDirect(mVertexTextureArray.size * java.lang.Float.SIZE / 8)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexTextureBuffer.put(mVertexTextureArray).position(0)
        mIndexBuffer = ByteBuffer.allocateDirect(mIndexArray.size * java.lang.Integer.SIZE / 8)
            .order(ByteOrder.nativeOrder()).asIntBuffer()
        mIndexBuffer.put(mIndexArray).position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d(TAG, "onSurfaceCreated")
        mCameraTextureId = TextureUtils.loadOESTexture()
        mSurfaceTexture = SurfaceTexture(mCameraTextureId)
        val vertexId =
            ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.mediacodec_t2_vertex))
        val fragmentId =
            ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.mediacodec_t2_fragment))
        mProgramId = ShaderUtils.linkProgram(vertexId, fragmentId)

        GLES30.glUseProgram(mProgramId)
        mVertexPositionLocation = GLES30.glGetAttribLocation(mProgramId, "position")
        mTexturePositionLocation = GLES30.glGetAttribLocation(mProgramId, "texturePositionIn")
        mTextureLocation = GLES30.glGetUniformLocation(mProgramId, "img")
        mMatrixLocation = GLES30.glGetUniformLocation(mProgramId, "matrix")
        GLES30.glUseProgram(0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        Log.d(TAG, "onDrawFrame")
        GLES30.glUseProgram(mProgramId)
        mSurfaceTexture?.updateTexImage()
        mSurfaceTexture?.getTransformMatrix(mMatrixArray)

        GLES30.glEnableVertexAttribArray(mVertexPositionLocation)
        GLES30.glVertexAttribPointer(
            mVertexPositionLocation, 2, GLES30.GL_FLOAT,
            false, 16, mVertexTextureBuffer.position(0)
        )
        GLES30.glEnableVertexAttribArray(mTexturePositionLocation)
        GLES30.glVertexAttribPointer(
            mTexturePositionLocation, 2, GLES30.GL_FLOAT,
            false, 16, mVertexTextureBuffer.position(2)
        )
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mCameraTextureId)
        GLES30.glUniform1i(mTextureLocation, 0)
        GLES30.glUniformMatrix4fv(mMatrixLocation, 1, false, mMatrixArray, 0)

        GLES30.glClearColor(1f, 1f, 1f, 1f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glDrawElements(
            GLES30.GL_TRIANGLES, mIndexArray.size, GLES30.GL_UNSIGNED_INT, mIndexBuffer
        )

    }
}