package com.example.openglesdemo1.ui.mytest1.t1

import android.opengl.GLES30
import android.opengl.Matrix
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class ColorFilter {
    private var mProgramId = 0
    private var mALocation = 0
    private var mTextLocation = 0
    private var mImgLocation = 0
    private val mPoints = floatArrayOf(
        0f, 0f, 0.5f, 0.5f,
        -1f, -1f, 0f, 0f,
        1f, -1f, 1f, 0f,
        1f, 1f, 1f, 1f,
        -1f, 1f, 0f, 1f,
        -1f, -1f, 0f, 0f,
    )
    private val mByteBuffer: FloatBuffer
    var mColorTextureId = 0
    private var mMatrix = FloatArray(16)
    private var mMatrixLocation = 0

    init {
        mByteBuffer = ByteBuffer.allocateDirect(mPoints.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mByteBuffer.position(0)
        mByteBuffer.put(mPoints)
    }

    fun setMatrix(back: Boolean, p: Float) {
        if (!back) {
            Matrix.orthoM(mMatrix, 0, -1f, 1f, -p, p, -1f, 1f)
            Matrix.scaleM(mMatrix, 0, 1f, -1f, 1f)
        } else {
            Matrix.orthoM(mMatrix, 0, -1f, 1f, -p, p, -1f, 1f)
        }
    }

    fun onSurfaceCreate() {
        val vertexId =
            ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.mytest1_vertex2))
        val fragmentId =
            ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.mytest1_fragment2))
        mProgramId = ShaderUtils.linkProgram(vertexId, fragmentId)
        GLES30.glClearColor(0f, 0f, 0f, 1f)
        mALocation = GLES30.glGetAttribLocation(mProgramId, "aPosition")
        mTextLocation = GLES30.glGetAttribLocation(mProgramId, "aTextCoord")
        mImgLocation = GLES30.glGetUniformLocation(mProgramId, "img")
        mMatrixLocation = GLES30.glGetUniformLocation(mProgramId, "matrix")
    }

    fun onSurfaceChanged(width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    fun onDraw() {
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
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mColorTextureId)
        GLES30.glUniform1i(mImgLocation, 0)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 6)

        GLES30.glDisableVertexAttribArray(mALocation)
        GLES30.glDisableVertexAttribArray(mTextLocation)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        GLES30.glUseProgram(0)
    }

}