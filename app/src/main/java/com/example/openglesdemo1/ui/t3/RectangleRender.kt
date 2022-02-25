package com.example.openglesdemo1.ui.t3

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.openglesdemo1.utils.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class RectangleRender : GLSurfaceView.Renderer {
    private var mMatrix = FloatArray(16)
    private val mVertexBuf: FloatBuffer
    private var mUMatrixLocation = 0
    private var mProgram = 0
    private var mAPositionLocation = 0
    private var mAColorLocation = 0
    private val POSITION_COMPONENT_COUNT = 2
    private val COLOR_COMPONENT_COUNT = 3

    private val BYTES_PER_FLOAT = 4

    private val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT
    private val mVertexPoints = floatArrayOf(
        0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
        -0.5f, -0.8f, 1.0f, 1.0f, 1.0f,
        0.5f, -0.8f, 1.0f, 1.0f, 1.0f,
        0.5f, 0.8f, 1.0f, 1.0f, 1.0f,
        -0.5f, 0.8f, 1.0f, 1.0f, 1.0f,
        -0.5f, -0.8f, 1.0f, 1.0f, 1.0f,

        0.0f, 0.25f, 0.5f, 0.5f, 0.5f,
        0.0f, -0.25f, 0.5f, 0.5f, 0.5f,
    )
    private val mVertexShaderStr = """
        #version 300 es
        layout (location = 0) in vec4 vPosition;
        layout (location = 1) in vec4 aColor;
        uniform mat4 u_Matrix;
        out vec4 vColor;
        void main(){
            gl_Position = u_Matrix * vPosition;
            gl_PointSize = 10.0;
            vColor = aColor;
        }
    """.trimIndent()

    private val mFragmentShaderStr = """
        #version 300 es
        precision mediump float;
        in vec4 vColor;
        out vec4 fragColor;
        void main(){
        fragColor = vColor;
        }
    """.trimIndent()

    init {
        mVertexBuf = ByteBuffer.allocateDirect(mVertexPoints.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mVertexBuf.put(mVertexPoints).position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f)

        var vertexShaderId = ShaderUtils.compileVertexShader(mVertexShaderStr)
        var fragmentShaderId = ShaderUtils.compileFragmentShader(mFragmentShaderStr)
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId)

        GLES30.glUseProgram(mProgram)
        mUMatrixLocation = GLES30.glGetUniformLocation(mProgram, "u_Matrix")
        mAPositionLocation = GLES30.glGetAttribLocation(mProgram, "vPosition")
        mAColorLocation = GLES30.glGetAttribLocation(mProgram, "aColor")

        mVertexBuf.position(0)
        GLES30.glVertexAttribPointer(
            mAPositionLocation, POSITION_COMPONENT_COUNT, GLES30.GL_FLOAT, false, STRIDE, mVertexBuf
        )
        GLES30.glEnableVertexAttribArray(mAPositionLocation)

        mVertexBuf.position(POSITION_COMPONENT_COUNT);
        GLES30.glVertexAttribPointer(
            mAColorLocation,
            COLOR_COMPONENT_COUNT,
            GLES30.GL_FLOAT,
            false,
            STRIDE,
            mVertexBuf
        )
        GLES30.glEnableVertexAttribArray(mAColorLocation)

        var maxLength = IntArray(1)
        GLES30.glGetProgramiv(mProgram, GLES30.GL_ACTIVE_UNIFORM_MAX_LENGTH, maxLength, 0)
        var size = IntArray(1)
        GLES30.glGetProgramiv(mProgram, GLES30.GL_ACTIVE_UNIFORMS, size, 0)
        println("RectangleRender  maxlength=${maxLength[0]},size=${size[0]}")

        //遍历uniform
        var length = IntArray(1)
        var size2 = IntArray(1)
        var type = IntArray(1)
        var nameBuffer = ByteArray(maxLength[0] - 1)
        for (index in 0..(size[0] - 1)) {
            GLES30.glGetActiveUniform(
                mProgram,
                index,
                maxLength[0],
                length,
                0,
                size2,
                0,
                type,
                0,
                nameBuffer,
                0
            )
            var name = String(nameBuffer)
            var location = GLES30.glGetUniformLocation(mProgram, name)
            println("RectangleRender  length=${length[0]},size2=${size2[0]},type=${type[0]},name=${name},location=${location}")
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val aspectRatio: Float =
            if (width > height) width * 1.0f / height else 1.0f * height / width
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
        GLES30.glUniformMatrix4fv(mUMatrixLocation, 1, false, mMatrix, 0)
        //绘制矩形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 6)
        //绘制两个点
        GLES30.glDrawArrays(GLES30.GL_POINTS, 6, 2)
    }
}