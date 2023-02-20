package com.example.openglesdemo1.ui.stu1.t12.filter

import android.opengl.GLES30
import android.opengl.Matrix
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.AppCore
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import com.example.openglesdemo1.utils.TextureUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

open class BaseFilter : RendererFilter {
    private var mVertexBuffer: FloatBuffer? = null
    private var mTexVertexBuffer: FloatBuffer? = null
    private var mVertexIndexBuffer: ShortBuffer? = null
    var mProgram = 0
    private var mTextureId = 0
    private var uMatrixLocation = 0

    /**
     * 矩阵
     */
    private var mMatrix = FloatArray(16)

    /**
     * 顶点着色器
     */
    private var mVertexShader: String = ""

    /**
     * 片段着色器
     */
    private var mFragmentShader: String = ""

    /**
     * 顶点坐标
     * (x,y,z)
     */
    private val POSITION_VERTEX = floatArrayOf(
        0f, 0f, 0f,     //顶点坐标V0
        1f, 1f, 0f,     //顶点坐标V1
        -1f, 1f, 0f,    //顶点坐标V2
        -1f, -1f, 0f,   //顶点坐标V3
        1f, -1f, 0f //顶点坐标V4
    )

    /**
     * 纹理坐标
     * (s,t)
     */
    private val TEX_VERTEX = floatArrayOf(
        0.5f, 0.5f, //纹理坐标V0
        1f, 0f,     //纹理坐标V1
        0f, 0f,     //纹理坐标V2
        0f, 1.0f,   //纹理坐标V3
        1f, 1.0f //纹理坐标V4
    )

    /**
     * 索引
     */
    private val VERTEX_INDEX = shortArrayOf(
        0, 1, 2,  //V0,V1,V2 三个顶点组成一个三角形
        0, 2, 3,  //V0,V2,V3 三个顶点组成一个三角形
        0, 3, 4,  //V0,V3,V4 三个顶点组成一个三角形
        0, 4, 1 //V0,V4,V1 三个顶点组成一个三角形
    )

    init {
        initFilter(
            ResReadUtils.readResource(R.raw.no_filter_vertex_shader),
            ResReadUtils.readResource(R.raw.no_filter_fragment_shader)
        )
    }

    open fun initFilter(vertexShader: String, fragmentShader: String) {
        mVertexShader = vertexShader
        mFragmentShader = fragmentShader
        mVertexBuffer = ByteBuffer.allocateDirect(POSITION_VERTEX.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mVertexBuffer?.put(POSITION_VERTEX)?.position(0)
        mTexVertexBuffer = ByteBuffer.allocateDirect(TEX_VERTEX.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mTexVertexBuffer?.put(TEX_VERTEX)?.position(0)
        mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
        mVertexIndexBuffer?.put(VERTEX_INDEX)?.position(0)
    }

    override fun onSurfaceCreated() {
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f)
        var vertexShaderId = ShaderUtils.compileVertexShader(mVertexShader)
        var fragmentShaderId = ShaderUtils.compileFragmentShader(mFragmentShader)
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId)
        uMatrixLocation = GLES30.glGetUniformLocation(mProgram, "u_Matrix")
        //加载纹理
        mTextureId = TextureUtils.loadTexture(AppCore.getContext(), R.mipmap.main)
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val aspectRatio =
            if (width > height) width.toFloat() / height.toFloat() else height.toFloat() / width.toFloat()
        if (width > height) {
            //横屏
            Matrix.orthoM(mMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
        } else {
            //竖屏
            Matrix.orthoM(mMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f)
        }
    }

    override fun onDrawFrame() {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glUseProgram(mProgram)

        //更新属性等信息
        onUpdateDrawFrame()

        GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0)

        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, mVertexBuffer)

        GLES30.glEnableVertexAttribArray(1)
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 0, mTexVertexBuffer)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        //绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId)
        // 绘制
        GLES30.glDrawElements(
            GLES30.GL_TRIANGLES,
            VERTEX_INDEX.size,
            GLES30.GL_UNSIGNED_SHORT,
            mVertexIndexBuffer
        )
    }

    open fun onUpdateDrawFrame() {}

    override fun onDestroy() {
        GLES30.glDeleteProgram(mProgram)
    }
}