package com.example.openglesdemo1.ui.camera.t6

import android.content.Context
import android.opengl.GLES30
import android.opengl.Matrix
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import com.example.openglesdemo1.utils.TextureUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class ColorFilter(val mContext: Context) {
    private var mProgramId = 0
    private var mALocation = 0
    private var mTextLocation = 0
    private var mImgLocation = 0
    private var mFlagLocation = 0
    private var mLutLocation = 0
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
    private var mLutTextureId = 0
    private var mMatrix = FloatArray(16)
    private var mMatrixLocation = 0
    private var mFlag = 0
    private val mFlagList = arrayListOf(
        "无滤镜", "lut滤镜1", "lut滤镜2", "设置灰度", "黑白",
        "反向", "亮度", "亮度2", "色调分离"
    )

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

    fun transYMatrix() {
        Matrix.scaleM(mMatrix, 0, 1f, -1f, 1f)
    }

    fun changeFilter() {
        mFlag = ++mFlag % mFlagList.size
        setFilter(mFlagList[mFlag])
        when (mFlag) {
            1 -> {
                GLES30.glDeleteTextures(1, intArrayOf(mLutTextureId), 0)
                mLutTextureId = TextureUtils.loadTexture(mContext, R.mipmap.img_lut1)
            }
            2 -> {
                GLES30.glDeleteTextures(1, intArrayOf(mLutTextureId), 0)
                mLutTextureId = TextureUtils.loadTexture(mContext, R.mipmap.img_lut3)
            }
        }
    }

    fun onSurfaceCreate() {
        val vertexId =
            ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.camera_t6_vertex2))
        val fragmentId =
            ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.camera_t6_fragment2))
        mProgramId = ShaderUtils.linkProgram(vertexId, fragmentId)
        GLES30.glClearColor(0f, 0f, 0f, 1f)
        mALocation = GLES30.glGetAttribLocation(mProgramId, "aPosition")
        mTextLocation = GLES30.glGetAttribLocation(mProgramId, "aTextCoord")
        mImgLocation = GLES30.glGetUniformLocation(mProgramId, "img")
        mMatrixLocation = GLES30.glGetUniformLocation(mProgramId, "matrix")
        mFlagLocation = GLES30.glGetUniformLocation(mProgramId, "filterFlag")
        mLutLocation = GLES30.glGetUniformLocation(mProgramId, "lut")
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

        GLES30.glUniform1i(mFlagLocation, mFlag)
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mColorTextureId)
        GLES30.glUniform1i(mImgLocation, 0)
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mLutTextureId)
        GLES30.glUniform1i(mLutLocation, 1)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 6)

        GLES30.glDisableVertexAttribArray(mALocation)
        GLES30.glDisableVertexAttribArray(mTextLocation)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        GLES30.glUseProgram(0)
    }

    private fun setFilter(str: String) {
        if (mContext is ChangeFilterActivity) {
            val activity = mContext as ChangeFilterActivity
            activity.setFilterStr(str)
        }
    }

}