package com.example.openglesdemo1.ui.t6

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SquareRender : GLSurfaceView.Renderer {
    private val mVertexBuffer: FloatBuffer
    private var mProgram: Int = 0
    private val POSITION_COMPONENT_COUNT = 3
    private val mVertexPointData = floatArrayOf(
        0.25f, 0.25f, 0.0f,  //V0
        -0.75f, 0.25f, 0.0f, //V1
        -0.75f, -0.75f, 0.0f, //V2
        0.25f, -0.75f, 0.0f, //V3

        0.75f, -0.25f, 0.0f, //V4
        0.75f, 0.75f, 0.0f, //V5
        -0.25f, 0.75f, 0.0f, //V6
        -0.25f, -0.25f, 0.0f, //V7

        -0.25f, 0.75f, 0.0f, //V6
        -0.75f, 0.25f, 0.0f, //V1

        0.75f, 0.75f, 0.0f, //V5
        0.25f, 0.25f, 0.0f, //V0

        -0.25f, -0.25f, 0.0f, //V7
        -0.75f, -0.75f, 0.0f, //V2

        0.75f, -0.25f, 0.0f, //V4
        0.25f, -0.75f, 0.0f //V3
    )

    init {
        mVertexBuffer = ByteBuffer.allocateDirect(mVertexPointData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexBuffer.put(mVertexPointData).position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        var vertexShaderId =
            ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_linecube_shader))
        var fragmentShaderId =
            ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_linecube_shader))
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId)
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f)
        GLES30.glUseProgram(mProgram)
        GLES30.glVertexAttribPointer(
            0,
            POSITION_COMPONENT_COUNT,
            GLES30.GL_FLOAT,
            false,
            0,
            mVertexBuffer
        )
        GLES30.glEnableVertexAttribArray(0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glLineWidth(5.0f)
        GLES30.glDrawArrays(GLES30.GL_LINE_LOOP, 0, 4)
        GLES30.glDrawArrays(GLES30.GL_LINE_LOOP, 4, 4)
        GLES30.glDrawArrays(GLES30.GL_LINES, 8, 8)
    }
}