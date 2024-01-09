package com.example.openglesdemo1.ui.normal.t6

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
    private val mColorBuffer: FloatBuffer
    private var mProgram: Int = 0
    private val POSITION_COMPONENT_COUNT = 3
    private val VERTEX_COLOR_SIZE = 4
    private val mVertexPointData = floatArrayOf(
        //背面矩形
        0.75f, 0.75f, 0.0f, //V5
        -0.25f, 0.75f, 0.0f, //V6
        -0.25f, -0.25f, 0.0f, //V7
        0.75f, 0.75f, 0.0f, //V5
        -0.25f, -0.25f, 0.0f, //V7
        0.75f, -0.25f, 0.0f, //V4

        //左侧矩形
        -0.25f, 0.75f, 0.0f, //V6
        -0.75f, 0.25f, 0.0f, //V1
        -0.75f, -0.75f, 0.0f, //V2
        -0.25f, 0.75f, 0.0f, //V6
        -0.75f, -0.75f, 0.0f, //V2
        -0.25f, -0.25f, 0.0f, //V7

        //底部矩形
        0.75f, -0.25f, 0.0f, //V4
        -0.25f, -0.25f, 0.0f, //V7
        -0.75f, -0.75f, 0.0f, //V2
        0.75f, -0.25f, 0.0f, //V4
        -0.75f, -0.75f, 0.0f, //V2
        0.25f, -0.75f, 0.0f, //V3

        //正面矩形
        0.25f, 0.25f, 0.0f,  //V0
        -0.75f, 0.25f, 0.0f, //V1
        -0.75f, -0.75f, 0.0f, //V2
        0.25f, 0.25f, 0.0f,  //V0
        -0.75f, -0.75f, 0.0f, //V2
        0.25f, -0.75f, 0.0f, //V3

        //右侧矩形
        0.75f, 0.75f, 0.0f, //V5
        0.25f, 0.25f, 0.0f, //V0
        0.25f, -0.75f, 0.0f, //V3
        0.75f, 0.75f, 0.0f, //V5
        0.25f, -0.75f, 0.0f, //V3
        0.75f, -0.25f, 0.0f, //V4

        //顶部矩形
        0.75f, 0.75f, 0.0f, //V5
        -0.25f, 0.75f, 0.0f, //V6
        -0.75f, 0.25f, 0.0f, //V1
        0.75f, 0.75f, 0.0f, //V5
        -0.75f, 0.25f, 0.0f, //V1
        0.25f, 0.25f, 0.0f  //V0
    )

    //立方体的顶点颜色
    private val mColorsData = floatArrayOf(
        //背面矩形颜色
        1f, 0f, 1f, 1f,
        1f, 0f, 1f, 1f,
        1f, 0f, 1f, 1f,
        1f, 0f, 1f, 1f,
        1f, 0f, 1f, 1f,
        1f, 0f, 1f, 1f,
        //左侧矩形颜色
        0f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        //底部矩形颜色
        1f, 0f, 0.5f, 1f,
        1f, 0f, 0.5f, 1f,
        1f, 0f, 0.5f, 1f,
        1f, 0f, 0.5f, 1f,
        1f, 0f, 0.5f, 1f,
        1f, 0f, 0.5f, 1f,
        //正面矩形颜色
        0.2f, 0.3f, 0.2f, 1f,
        0.2f, 0.3f, 0.2f, 1f,
        0.2f, 0.3f, 0.2f, 1f,
        0.2f, 0.3f, 0.2f, 1f,
        0.2f, 0.3f, 0.2f, 1f,
        0.2f, 0.3f, 0.2f, 1f,
        //右侧矩形颜色
        0.1f, 0.2f, 0.3f, 1f,
        0.1f, 0.2f, 0.3f, 1f,
        0.1f, 0.2f, 0.3f, 1f,
        0.1f, 0.2f, 0.3f, 1f,
        0.1f, 0.2f, 0.3f, 1f,
        0.1f, 0.2f, 0.3f, 1f,
        //顶部矩形颜色
        0.3f, 0.4f, 0.5f, 1f,
        0.3f, 0.4f, 0.5f, 1f,
        0.3f, 0.4f, 0.5f, 1f,
        0.3f, 0.4f, 0.5f, 1f,
        0.3f, 0.4f, 0.5f, 1f,
        0.3f, 0.4f, 0.5f, 1f
    )

    init {
        mVertexBuffer = ByteBuffer.allocateDirect(mVertexPointData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexBuffer.put(mVertexPointData).position(0)
        mColorBuffer = ByteBuffer.allocateDirect(mColorsData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mColorBuffer.put(mColorsData).position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        var vertexShaderId =
            ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.normal_t6_vertex_linecube_shader))
        var fragmentShaderId =
            ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.normal_t6_fragment_linecube_shader))
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
        GLES30.glVertexAttribPointer(1, VERTEX_COLOR_SIZE, GLES30.GL_FLOAT, false, 0, mColorBuffer)
        //启用颜色顶点属性
        GLES30.glEnableVertexAttribArray(1);

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36)
    }
}