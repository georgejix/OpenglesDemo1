package com.example.openglesdemo1.ui.t4

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

class VertexBufferRenderer : GLSurfaceView.Renderer {
    private val mVertexPoints = floatArrayOf(
        0.0f, 0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f,
    )
    private var mVertexBuf: FloatBuffer
    private var mProgram = 0
    private var VERTEX_POS_INDEX = 0
    private var VERTEX_POS_SIZE = 3
    private var VERTEX_STRIDE = VERTEX_POS_SIZE * 4
    private var vboIds = IntArray(1)

    init {
        mVertexBuf = ByteBuffer.allocateDirect(mVertexPoints.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mVertexBuf.put(mVertexPoints).position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f)
        //编译
        val vertexShaderId =
            ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_buffer_shader))
        val fragmentShaderId =
            ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_buffer_shader))
        //鏈接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId)
        //1. 生成1个缓冲ID
        GLES30.glGenBuffers(1, vboIds, 0)

        //2. 绑定到顶点坐标数据缓冲，切换当前缓冲区到刚刚生成的缓冲区上
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboIds[0])
        //3. 向顶点坐标数据缓冲送入数据
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            mVertexPoints.size * 4,
            mVertexBuf,
            GLES30.GL_STATIC_DRAW
        )

        //4. 将顶点位置数据送入渲染管线
        GLES30.glVertexAttribPointer(
            VERTEX_POS_INDEX,
            VERTEX_POS_SIZE,
            GLES30.GL_FLOAT,
            false,
            VERTEX_STRIDE,
            0
        )
        //解绑VBO,切换到无用缓冲区
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        //5. 启用顶点位置属性
        GLES30.glEnableVertexAttribArray(VERTEX_POS_INDEX)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glUseProgram(mProgram)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3)
    }
}