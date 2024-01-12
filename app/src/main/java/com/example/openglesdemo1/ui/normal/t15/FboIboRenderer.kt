package com.example.openglesdemo1.ui.normal.t15

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
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

class FboIboRenderer(val context: Context) : GLSurfaceView.Renderer {

    private var mVertexTextureBuffer: FloatBuffer
    private var mIndexBuffer: IntBuffer

    private var mProgramId = 0
    private var mTextureId = 0
    private var mVertexPositionLocation = 0
    private var mTexturePositionLocation = 0
    private var mTextureLocation = 0
    private var mMatrixLocation = 0
    private var mFboId = 0
    private var mIboId = 0
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

    init {
        mVertexTextureBuffer =
            ByteBuffer.allocateDirect(mVertexTextureArray.size * java.lang.Float.SIZE)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexTextureBuffer.put(mVertexTextureArray).position(0)
        mIndexBuffer = ByteBuffer.allocateDirect(mIndexArray.size * java.lang.Integer.SIZE)
            .order(ByteOrder.nativeOrder()).asIntBuffer()
        mIndexBuffer.put(mIndexArray).position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val vertexId =
            ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.normal_t15_vertex))
        val fragmentId =
            ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.normal_t15_fragment))
        mProgramId = ShaderUtils.linkProgram(vertexId, fragmentId)

        GLES30.glUseProgram(mProgramId)
        mVertexPositionLocation = GLES30.glGetAttribLocation(mProgramId, "vertexPosition")
        mTexturePositionLocation = GLES30.glGetAttribLocation(mProgramId, "texturePosition")
        mTextureLocation = GLES30.glGetUniformLocation(mProgramId, "texture")
        mMatrixLocation = GLES30.glGetUniformLocation(mProgramId, "matrix")

        mTextureId = TextureUtils.loadTexture(context, R.mipmap.main)

        val buffer = IntArray(2)
        GLES30.glGenBuffers(buffer.size, buffer, 0)
        mFboId = buffer[0]
        mIboId = buffer[1]

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mFboId)
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            mVertexTextureBuffer.capacity() * java.lang.Float.SIZE / 8,
            mVertexTextureBuffer,
            GLES30.GL_STATIC_DRAW
        )
        GLES30.glEnableVertexAttribArray(mVertexPositionLocation)
        GLES30.glVertexAttribPointer(
            mVertexPositionLocation,
            2,
            GLES30.GL_FLOAT,
            false,
            16,
            0
        )
        GLES30.glEnableVertexAttribArray(mTexturePositionLocation)
        GLES30.glVertexAttribPointer(
            mTexturePositionLocation,
            2,
            GLES30.GL_FLOAT,
            false,
            16,
            8
        )
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mIboId)
        GLES30.glBufferData(
            GLES30.GL_ELEMENT_ARRAY_BUFFER,
            mIndexBuffer.capacity() * Integer.SIZE / 8,
            mIndexBuffer, GLES30.GL_STATIC_DRAW
        )
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        Matrix.orthoM(
            mMatrixArray, 0, -1f, 1f,
            -height * 1f / width, height * 1f / width, -1f, 1f
        )
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glUseProgram(mProgramId)
        GLES30.glClearColor(0.9f, 0.9f, 0.9f, 1f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        GLES30.glUniformMatrix4fv(mMatrixLocation, 1, false, mMatrixArray, 0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId)

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mFboId)
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mIboId)
        GLES30.glDrawElements(
            GLES30.GL_TRIANGLES,
            mIndexArray.size,
            GLES30.GL_UNSIGNED_INT,
            0
        )

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)
        GLES30.glUseProgram(0)
    }
}