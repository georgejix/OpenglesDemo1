package com.example.openglesdemo1.ui.stu3.t2

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Test2Render(val mContext: Context) : GLSurfaceView.Renderer {
    var mProgramId = 0
    val A_COLOR = "a_Color"
    var aColorLocation = 0
    val A_POSITION = "a_Position"
    var aPositionLocation = 0
    val U_MATRIX = "u_Matrix"
    var uMatrixLocation = 0
    var mVertexBuffer: FloatBuffer
    val mPoints = floatArrayOf(
        0f, 0f, 0f, 0f, 0f,
        -0.5f, -0.5f, 0f, 0f, 0f,
        0.5f, -0.5f, 0f, 0f, 0f,
        0.5f, 0.5f, 0f, 0f, 0f,
        -0.5f, 0.5f, 0f, 0f, 0f,
        -0.5f, -0.5f, 0f, 0f, 0f,

        0.5f, 0f, 1f, 0f, 0f,
        -0.5f, 0f, 1f, 0f, 0f,

        0f, -0.25f, 0f, 0f, 1f,
        0f, 0.25f, 0f, 0f, 1f,
    )
    val uMatrixArray = FloatArray(16)

    init {
        mVertexBuffer = ByteBuffer.allocateDirect(mPoints.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mVertexBuffer.put(mPoints)
        mVertexBuffer.position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val vertexId =
            ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_test2))
        val fragmentId =
            ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_test2))
        mProgramId = ShaderUtils.linkProgram(vertexId, fragmentId)
        GLES30.glClearColor(1f, 1f, 1f, 1f)
        aColorLocation = GLES30.glGetAttribLocation(mProgramId, A_COLOR)
        aPositionLocation = GLES30.glGetAttribLocation(mProgramId, A_POSITION)
        uMatrixLocation = GLES30.glGetUniformLocation(mProgramId, U_MATRIX)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val p = height.toFloat() / width.toFloat()
        Matrix.orthoM(uMatrixArray, 0, -1f, 1f, -p, p, -1f, 1f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glUseProgram(mProgramId)
        mVertexBuffer.position(0)
        GLES30.glVertexAttribPointer(
            aPositionLocation,
            2,
            GLES30.GL_FLOAT,
            false,
            5 * 4,
            mVertexBuffer
        )
        GLES30.glEnableVertexAttribArray(aPositionLocation)

        mVertexBuffer.position(2)
        GLES30.glVertexAttribPointer(
            aColorLocation,
            3,
            GLES30.GL_FLOAT,
            false,
            5 * 4,
            mVertexBuffer
        )
        GLES30.glEnableVertexAttribArray(aColorLocation)

        GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, uMatrixArray, 0)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 6)
        GLES30.glDrawArrays(GLES30.GL_LINES, 6, 2)
        GLES30.glDrawArrays(GLES30.GL_POINTS, 8, 1)
        GLES30.glDrawArrays(GLES30.GL_POINTS, 9, 1)

        GLES30.glDisableVertexAttribArray(aPositionLocation)
        GLES30.glDisableVertexAttribArray(aColorLocation)
        GLES30.glUseProgram(0)
    }
}