package com.example.openglesdemo1.ui.stu1.t7

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SquareRender : GLSurfaceView.Renderer {
    private val mVertexBuffer: FloatBuffer
    private val mColorBuffer: FloatBuffer
    private val mIndicesBuffer: ShortBuffer
    private var mProgram: Int = 0
    private val POSITION_COMPONENT_COUNT = 3
    private val VERTEX_COLOR_SIZE = 4
    private val mVertexPointData = floatArrayOf(
        //正面矩形
        0.25f, 0.25f, 0.0f,  //V0
        -0.75f, 0.25f, 0.0f, //V1
        -0.75f, -0.75f, 0.0f, //V2
        0.25f, -0.75f, 0.0f, //V3

        //背面矩形
        0.75f, -0.25f, 0.0f, //V4
        0.75f, 0.75f, 0.0f, //V5
        -0.25f, 0.75f, 0.0f, //V6
        -0.25f, -0.25f, 0.0f //V7
    )

    //立方体的顶点颜色
    private val mColorsData = floatArrayOf(
        0.3f, 0.4f, 0.5f, 1f,   //V0
        0.3f, 0.4f, 0.5f, 1f,   //V1
        0.3f, 0.4f, 0.5f, 1f,   //V2
        0.3f, 0.4f, 0.5f, 1f,   //V3
        0.6f, 0.5f, 0.4f, 1f,   //V4
        0.6f, 0.5f, 0.4f, 1f,   //V5
        0.6f, 0.5f, 0.4f, 1f,   //V6
        0.6f, 0.5f, 0.4f, 1f    //V7
    )

    //立方体的顶点索引
    private val mIndicesData = shortArrayOf(
        //背面
        5, 6, 7, 5, 7, 4,
        //左侧
        6, 1, 2, 6, 2, 7,
        //底部
        4, 7, 2, 4, 2, 3,
        //顶面
        5, 6, 7, 5, 7, 4,
        //右侧
        5, 0, 3, 5, 3, 4,
        //正面
        0, 1, 2, 0, 2, 3
    )

    init {
        mVertexBuffer = ByteBuffer.allocateDirect(mVertexPointData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexBuffer.put(mVertexPointData).position(0)
        mColorBuffer = ByteBuffer.allocateDirect(mColorsData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mColorBuffer.put(mColorsData).position(0)
        mIndicesBuffer = ByteBuffer.allocateDirect(mIndicesData.size * 4)
            .order(ByteOrder.nativeOrder()).asShortBuffer()
        mIndicesBuffer.put(mIndicesData).position(0)
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
        GLES30.glVertexAttribPointer(1, VERTEX_COLOR_SIZE, GLES30.GL_FLOAT, false, 0, mColorBuffer)
        //启用颜色顶点属性
        GLES30.glEnableVertexAttribArray(1);

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glDrawElements(
            GLES30.GL_TRIANGLES,
            mIndicesData.size,
            GLES30.GL_UNSIGNED_SHORT,
            mIndicesBuffer
        )
    }
}