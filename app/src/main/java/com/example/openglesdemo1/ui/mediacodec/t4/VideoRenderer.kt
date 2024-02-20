package com.example.openglesdemo1.ui.mediacodec.t4

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import android.view.Surface
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

class VideoRenderer(val mPlay: (() -> Unit)) : GLSurfaceView.Renderer {
    private val TAG = "VideoRenderer"
    private var mSurfaceTexture: SurfaceTexture? = null
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

    private var mVboId = 0
    private var mIboId = 0
    private var mVertexTextureBuffer: FloatBuffer
    private var mIndexBuffer: IntBuffer
    private var mTextureId = -1

    init {
        mVertexTextureBuffer =
            ByteBuffer.allocateDirect(mVertexTextureArray.size * java.lang.Float.SIZE / 8)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexTextureBuffer.put(mVertexTextureArray).position(0)
        mIndexBuffer = ByteBuffer.allocateDirect(mIndexArray.size * java.lang.Integer.SIZE / 8)
            .order(ByteOrder.nativeOrder()).asIntBuffer()
        mIndexBuffer.put(mIndexArray).position(0)
    }

    fun getSurface(): Surface = Surface(mSurfaceTexture)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val vertexId =
            ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.mediacodec_t4_vertex))
        val fragmentId =
            ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.mediacodec_t4_fragment))
        mProgramId = ShaderUtils.linkProgram(vertexId, fragmentId)
        GLES30.glUseProgram(mProgramId)
        GLES30.glClearColor(1f, 1f, 1f, 1f)
        mVertexPositionLocation = GLES30.glGetAttribLocation(mProgramId, "position")
        mTexturePositionLocation = GLES30.glGetAttribLocation(mProgramId, "texturePositionIn")
        mTextureLocation = GLES30.glGetUniformLocation(mProgramId, "img")
        mMatrixLocation = GLES30.glGetUniformLocation(mProgramId, "matrix")
        Matrix.orthoM(mMatrixArray, 0, -1f, 1f, -1f, 1f, -1f, 1f)

        val buffer = IntArray(2)
        GLES30.glGenBuffers(buffer.size, buffer, 0)
        mVboId = buffer[0]
        mIboId = buffer[1]

        //vbo
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboId)
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            mVertexTextureBuffer.capacity() * java.lang.Float.SIZE / 8,
            mVertexTextureBuffer.position(0),
            GLES30.GL_STATIC_DRAW
        )
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)

        //ibo
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mIboId)
        GLES30.glBufferData(
            GLES30.GL_ELEMENT_ARRAY_BUFFER,
            mIndexBuffer.capacity() * Integer.SIZE / 8,
            mIndexBuffer, GLES30.GL_STATIC_DRAW
        )
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)
        mTextureId = TextureUtils.loadOESTexture()
        mSurfaceTexture = SurfaceTexture(mTextureId)
        mPlay()
        GLES30.glUseProgram(0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        Log.d(TAG, "onDrawFrame")
        GLES30.glUseProgram(mProgramId)

        runCatching {
            mSurfaceTexture?.updateTexImage()
        }

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId)
        GLES30.glUniform1i(mTextureLocation, 0)
        GLES30.glUniformMatrix4fv(mMatrixLocation, 1, false, mMatrixArray, 0)

        GLES30.glEnableVertexAttribArray(mVertexPositionLocation)
        GLES30.glEnableVertexAttribArray(mTexturePositionLocation)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboId)
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mIboId)
        GLES30.glVertexAttribPointer(
            mVertexPositionLocation, 2, GLES30.GL_FLOAT,
            false, 16, 0
        )
        GLES30.glVertexAttribPointer(
            mTexturePositionLocation, 2, GLES30.GL_FLOAT,
            false, 16, 2 * java.lang.Float.SIZE / 8
        )
        GLES30.glDrawElements(
            GLES30.GL_TRIANGLES, mIndexArray.size, GLES30.GL_UNSIGNED_INT, 0
        )
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)
        GLES30.glDisableVertexAttribArray(mVertexPositionLocation)
        GLES30.glDisableVertexAttribArray(mTexturePositionLocation)

        GLES30.glUseProgram(0)
    }
}