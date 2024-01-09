package com.example.openglesdemo1.ui.camera.t4

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES30
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class CameraTextureRender(val OESTextureId: Int) : ITextureRenderer {
    private var mVertexBuffer: FloatBuffer
    private var mProgram = -1//程序

    private var aPositionLocation = -1
    private var aTextureCoordLocation = -1
    private var uTextureMatrixLocation = -1
    private var uTextureSamplerLocation = -1

    private val POSITION_ATTRIBUTE = "vPosition"
    private val TEXTURE_COORD_ATTRIBUTE = "aTextureCoord"
    private val TEXTURE_MATRIX_UNIFORM = "uTextureMatrix"
    private val TEXTURE_SAMPLER_UNIFORM = "yuvTexSampler"

    private val POSITION_SIZE = 2
    private val TEXTURE_SIZE = 2
    private val STRIDE = (POSITION_SIZE + TEXTURE_SIZE) * 4

    /**
     * 前两个为顶点坐标
     * 后两个为纹理坐标
     */
    private val mVertexData = floatArrayOf(
        1.0f, 1.0f, 1.0f, 1.0f,
        -1.0f, 1.0f, 0.0f, 1.0f,
        -1.0f, -1f, 0.0f, 0.0f,
        1.0f, 1.0f, 1.0f, 1.0f,
        -1.0f, -1.0f, 0f, 0.0f,
        1.0f, -1.0f, 1.0f, 0.0f
    )

    private var mTransformMatrix = FloatArray(16)

    init {
        mVertexBuffer = ByteBuffer.allocateDirect(mVertexData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexBuffer.put(mVertexData).position(0)
    }

    override fun onSurfaceCreated() {
        val vertexShader =
            ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.camera_t2_vertex_camera_shader))
        val fragShader =
            ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.camera_t2_fragment_camera_shader))
        mProgram = ShaderUtils.linkProgram(vertexShader, fragShader)

        aPositionLocation = GLES30.glGetAttribLocation(mProgram, POSITION_ATTRIBUTE)
        aTextureCoordLocation = GLES30.glGetAttribLocation(mProgram, TEXTURE_COORD_ATTRIBUTE)
        uTextureMatrixLocation = GLES30.glGetUniformLocation(mProgram, TEXTURE_MATRIX_UNIFORM)
        uTextureSamplerLocation = GLES30.glGetUniformLocation(mProgram, TEXTURE_SAMPLER_UNIFORM)
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(surfaceTexture: SurfaceTexture?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glUseProgram(mProgram)
        surfaceTexture?.updateTexImage()
        surfaceTexture?.getTransformMatrix(mTransformMatrix)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, OESTextureId)
        GLES30.glUniform1i(uTextureSamplerLocation, 0)
        GLES30.glUniformMatrix4fv(uTextureMatrixLocation, 1, false, mTransformMatrix, 0)

        mVertexBuffer.position(0)
        GLES30.glEnableVertexAttribArray(aPositionLocation)
        GLES30.glVertexAttribPointer(
            aPositionLocation,
            2,
            GLES30.GL_FLOAT,
            false,
            STRIDE,
            mVertexBuffer
        )

        mVertexBuffer.position(2)
        GLES30.glEnableVertexAttribArray(aTextureCoordLocation)
        GLES30.glVertexAttribPointer(
            aTextureCoordLocation,
            2,
            GLES30.GL_FLOAT,
            false,
            STRIDE,
            mVertexBuffer
        )

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6)
    }
}