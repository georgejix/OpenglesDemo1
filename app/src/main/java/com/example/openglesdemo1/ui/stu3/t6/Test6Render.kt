package com.example.openglesdemo1.ui.stu3.t6

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import com.example.openglesdemo1.utils.TextureUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Test6Render(val mContext: Context) : GLSurfaceView.Renderer {
    private val TAG = "Test4Render"
    var mProgramId = 0
    var aPositionLocation = 0
    var texturePointLocation = 0
    var uMatrixLocation = 0
    var uImg1Location = 0
    var uImg2Location = 0
    var mImg1TextureId = 0
    var mImg2TextureId = 0

    val mDeskPoints = floatArrayOf(
        0f, 0f, 0.5f, 0.5f,
        -0.5f, -0.8f, 0f, 0.9f,
        0.5f, -0.8f, 1f, 0.9f,
        0.5f, 0.8f, 1f, 0.1f,
        -0.5f, 0.8f, 0f, 0.1f,
        -0.5f, -0.8f, 0f, 0.9f,
    )
    val uMatrixArray = FloatArray(16)
    val mFloatBuffer: FloatBuffer

    init {
        mFloatBuffer = ByteBuffer.allocateDirect(mDeskPoints.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(mDeskPoints)
        mFloatBuffer.position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val vertexId =
            ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_test6))
        val fragmentId =
            ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_test6))
        mProgramId = ShaderUtils.linkProgram(vertexId, fragmentId)
        GLES30.glClearColor(0f, 0f, 0f, 1f)
        aPositionLocation = GLES30.glGetAttribLocation(mProgramId, "a_Position")
        uMatrixLocation = GLES30.glGetUniformLocation(mProgramId, "u_Matrix")
        texturePointLocation = GLES30.glGetAttribLocation(mProgramId, "texturePoint")
        uImg1Location = GLES30.glGetUniformLocation(mProgramId, "img1")
        uImg2Location = GLES30.glGetUniformLocation(mProgramId, "img2")
        mImg1TextureId = TextureUtils.loadTexture(mContext, R.mipmap.img_bg3)
        mImg2TextureId = TextureUtils.loadTexture(mContext, R.mipmap.img_filter2)
        Log.d(
            TAG, "aPositionLocation=${aPositionLocation} uMatrixLocation=${uMatrixLocation} " +
                    "aTextureLocation=${texturePointLocation} uTextureUnitLocation=${uImg1Location}"
        )
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val p = height.toFloat() / width.toFloat()
        Matrix.orthoM(uMatrixArray, 0, -1f, 1f, -p, p, -1f, 1f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glUseProgram(mProgramId)

        mFloatBuffer.position(0)
        GLES30.glVertexAttribPointer(
            aPositionLocation, 2, GLES30.GL_FLOAT,
            false, 4 * 4, mFloatBuffer
        )
        GLES30.glEnableVertexAttribArray(aPositionLocation)

        mFloatBuffer.position(2)
        GLES30.glVertexAttribPointer(
            texturePointLocation, 2, GLES30.GL_FLOAT,
            false, 4 * 4, mFloatBuffer
        )
        GLES30.glEnableVertexAttribArray(texturePointLocation)

        GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, uMatrixArray, 0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mImg1TextureId)
        GLES30.glUniform1i(uImg1Location, 0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mImg2TextureId)
        GLES30.glUniform1i(uImg2Location, 1)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 6)

        GLES30.glDisableVertexAttribArray(aPositionLocation)
        GLES30.glDisableVertexAttribArray(texturePointLocation)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        GLES30.glUseProgram(0)
    }
}