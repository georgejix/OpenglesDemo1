package com.example.openglesdemo1.ui.normal2.t1

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Test1Renderer(val mContext: Context) : GLSurfaceView.Renderer {

    var mProgramId = 0
    val A_COLOR = "aColor"
    var aColorLocation = 0
    val A_POSITION = "a_Position"
    var aPositionLocation = 0
    var mVertexBuffer: FloatBuffer
    val mPoints = floatArrayOf(
        -0.5f, -0.5f,
        0.5f, 0.5f,
        -0.5f, 0.5f,

        -0.5f, -0.5f,
        0.5f, -0.5f,
        0.5f, 0.5f,

        0.5f, 0f,
        -0.5f, 0f,

        0f, -0.25f,
        0f, 0.25f,
    )

    init {
        mVertexBuffer = ByteBuffer.allocateDirect(mPoints.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mVertexBuffer.put(mPoints)
        mVertexBuffer.position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val vertexId = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER)
        if (0 == vertexId) {
            return
        }
        GLES30.glShaderSource(
            vertexId, ResReadUtils.readResource(R.raw.normal2_t1_vertex)
        )
        GLES30.glCompileShader(vertexId)
        val result = IntArray(1)
        GLES30.glGetShaderiv(vertexId, GLES30.GL_COMPILE_STATUS, result, 0)
        if (0 == result[0]) {
            GLES30.glDeleteShader(vertexId)
            return
        }

        val fragmentId = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER)
        if (0 == fragmentId) {
            return
        }
        GLES30.glShaderSource(
            fragmentId, ResReadUtils.readResource(R.raw.normal2_t1_fragment)
        )
        GLES30.glCompileShader(fragmentId)
        GLES30.glGetShaderiv(fragmentId, GLES30.GL_COMPILE_STATUS, result, 0)
        if (0 == result[0]) {
            GLES30.glDeleteShader(fragmentId)
            return
        }

        val programId = GLES30.glCreateProgram()
        GLES30.glAttachShader(programId, vertexId)
        GLES30.glAttachShader(programId, fragmentId)
        GLES30.glLinkProgram(programId)
        GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, result, 0)
        if (0 == result[0]) {
            GLES30.glDeleteProgram(programId)
            return
        }
        mProgramId = programId

        aColorLocation = GLES30.glGetUniformLocation(mProgramId, A_COLOR)
        aPositionLocation = GLES30.glGetAttribLocation(mProgramId, A_POSITION)
        GLES30.glClearColor(0f, 0f, 0f, 1f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glUseProgram(mProgramId)
        GLES30.glVertexAttribPointer(aPositionLocation, 2, GLES30.GL_FLOAT, false, 0, mVertexBuffer)
        GLES30.glEnableVertexAttribArray(aPositionLocation)

        GLES30.glEnableVertexAttribArray(aColorLocation)
        GLES30.glUniform4f(aColorLocation, 1f, 1f, 1f, 1f)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6)
        GLES30.glUniform4f(aColorLocation, 1f, 0f, 0f, 1f)
        GLES30.glDrawArrays(GLES30.GL_LINES, 6, 2)
        GLES30.glUniform4f(aColorLocation, 0f, 0f, 1f, 1f)
        GLES30.glDrawArrays(GLES30.GL_POINTS, 8, 1)
        GLES30.glUniform4f(aColorLocation, 0f, 0f, 1f, 1f)
        GLES30.glDrawArrays(GLES30.GL_POINTS, 9, 1)

        GLES30.glDisableVertexAttribArray(aPositionLocation)
        GLES30.glDisableVertexAttribArray(aColorLocation)
        GLES30.glUseProgram(0)
    }
}