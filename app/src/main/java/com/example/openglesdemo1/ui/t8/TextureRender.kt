package com.example.openglesdemo1.ui.t8

import android.opengl.GLES30
import android.opengl.GLSurfaceView
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
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TextureRender : GLSurfaceView.Renderer {
    var mVertexBuffer: FloatBuffer
    var mTexVertexBuffer: FloatBuffer
    var mVertexIndexBuffer: ShortBuffer
    var mProgram: Int = 0
    var mTextureId: Int = 0
    var uMatrixLocation: Int = 0
    var mMatrix = FloatArray(16)

    /**
     * 顶点坐标
     * (x,y,z)
     */
    val mVertexPointData = floatArrayOf(
        0f, 0f, 0f,     //顶点坐标V0
        1f, 1f, 0f,     //顶点坐标V1
        -1f, 1f, 0f,    //顶点坐标V2
        -1f, -1f, 0f,   //顶点坐标V3
        1f, -1f, 0f     //顶点坐标V4
    )

    /**
     * 纹理坐标
     * (s,t)
     */
    val mTexVertexData = floatArrayOf(
        0.5f, 0.5f, //纹理坐标V0
        1f, 0f,     //纹理坐标V1
        0f, 0f,     //纹理坐标V2
        0f, 1.0f,   //纹理坐标V3
        1f, 1.0f    //纹理坐标V4
    )

    /**
     * 索引
     */
    val mVertexIndexData = shortArrayOf(
        0, 1, 2,  //V0,V1,V2 三个顶点组成一个三角形
        0, 2, 3,  //V0,V2,V3 三个顶点组成一个三角形
        0, 3, 4,  //V0,V3,V4 三个顶点组成一个三角形
        0, 4, 1   //V0,V4,V1 三个顶点组成一个三角形
    )

    init {
        mVertexBuffer = ByteBuffer.allocateDirect(mVertexPointData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexBuffer.put(mVertexPointData).position(0)

        mTexVertexBuffer = ByteBuffer.allocateDirect(mTexVertexData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTexVertexBuffer.put(mTexVertexData).position(0)

        mVertexIndexBuffer = ByteBuffer.allocateDirect(mVertexIndexData.size * 4)
            .order(ByteOrder.nativeOrder()).asShortBuffer()
        mVertexIndexBuffer.put(mVertexIndexData).position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f)
        var vertexShader =
            ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_texture_shader))
        var fragmentShader =
            ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_texture_shader))
        mProgram = ShaderUtils.linkProgram(vertexShader, fragmentShader)

        uMatrixLocation = GLES30.glGetUniformLocation(mProgram, "u_Matrix")
        //加载纹理
        mTextureId = TextureUtils.loadTexture(AppCore.getContext(), R.mipmap.main)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
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

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        GLES30.glUseProgram(mProgram)
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
            mVertexIndexData.size,
            GLES30.GL_UNSIGNED_SHORT,
            mVertexIndexBuffer
        )
    }
}