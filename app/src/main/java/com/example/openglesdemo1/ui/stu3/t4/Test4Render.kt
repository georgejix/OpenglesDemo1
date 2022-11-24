package com.example.openglesdemo1.ui.stu3.t4

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import com.example.openglesdemo1.utils.TextureUtils
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Test4Render(val mContext: Context) : GLSurfaceView.Renderer {
    private val TAG = "Test4Render"
    var mProgramId = 0
    var aPositionLocation = 0
    var aTextureLocation = 0
    var uMatrixLocation = 0
    var uTextureUnitLocation = 0
    var mTextureId = 0

    val mDeskPoints = floatArrayOf(
        0f, 0f, 0.5f, 0.5f,
        -0.5f, -0.8f, 0f, 0.1f,
        0.5f, -0.8f, 1f, 0.1f,
        0.5f, 0.8f, 1f, 0.9f,
        -0.5f, 0.8f, 0f, 0.9f,
        -0.5f, -0.8f, 0f, 0.1f,
    )
    private var mDeskArray: VertexArray? = null
    val uMatrixArray = FloatArray(16)

    init {
        mDeskArray = VertexArray(mDeskPoints)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val vertexId =
            ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_test4))
        val fragmentId =
            ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_test4))
        mProgramId = ShaderUtils.linkProgram(vertexId, fragmentId)
        GLES30.glClearColor(0f, 0f, 0f, 1f)
        aPositionLocation = GLES30.glGetAttribLocation(mProgramId, "a_Position")
        uMatrixLocation = GLES30.glGetUniformLocation(mProgramId, "u_Matrix")
        aTextureLocation = GLES30.glGetAttribLocation(mProgramId, "aTexCoord")
        uTextureUnitLocation = GLES30.glGetUniformLocation(mProgramId, "u_textureUnit")
        mTextureId = TextureUtils.loadTexture(mContext, R.mipmap.img_bg2)
        Log.d(TAG,"aPositionLocation=${aPositionLocation} uMatrixLocation=${uMatrixLocation} " +
                "aTextureLocation=${aTextureLocation} uTextureUnitLocation=${uTextureUnitLocation}")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val p = height.toFloat() / width.toFloat()
        Matrix.orthoM(uMatrixArray, 0, -1f, 1f, -p, p, -1f, 1f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glUseProgram(mProgramId)

        mDeskArray?.setVertexAttriPointer(0, aPositionLocation, 2, 4 * 4)
        mDeskArray?.setVertexAttriPointer(2, aTextureLocation, 2, 4 * 4)

        GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, uMatrixArray, 0)
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId)
        GLES30.glUniform1i(uTextureUnitLocation, 0)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 6)

        GLES30.glDisableVertexAttribArray(aPositionLocation)
        GLES30.glDisableVertexAttribArray(aTextureLocation)
        GLES30.glUseProgram(0)
    }
}