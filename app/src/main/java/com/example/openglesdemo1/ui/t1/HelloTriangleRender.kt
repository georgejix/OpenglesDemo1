package com.example.openglesdemo1.ui.t1

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10


class HelloTriangleRender(var context: Context) : GLSurfaceView.Renderer {
    // Member variables
    private var mProgramObject: Int = 0
    private var mVertices: FloatBuffer
    private var mColorBuf: FloatBuffer
    private val TAG = "HelloTriangleRenderer"
    private val mVerticesData =
        floatArrayOf(0.0f, 0.5f, 0.0f, -0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f)
    private val mColorData = floatArrayOf(
        0.0f, 1.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f, 1.0f
    )

    init {
        mVertices = ByteBuffer.allocateDirect(mVerticesData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
        mColorBuf = ByteBuffer.allocateDirect(mColorData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColorBuf.put(mColorData).position(0);
    }

    ///
    // Create a shader object, load the shader source, and
    // compile the shader.
    //
    private fun loadShader(type: Int, shaderSrc: String): Int {
        val compiled = IntArray(1)

        // Create the shader object
        val shader = GLES30.glCreateShader(type)
        if (shader == 0) {
            return 0
        }

        // Load the shader source
        GLES30.glShaderSource(shader, shaderSrc)

        // Compile the shader
        GLES30.glCompileShader(shader)

        // Check the compile status
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            Log.e(TAG, GLES30.glGetShaderInfoLog(shader))
            GLES30.glDeleteShader(shader)
            return 0
        }
        return shader
    }

    ///
    // Initialize the shader and program object
    //
    override fun onSurfaceCreated(gl: GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        val vShaderStr = """#version 300 es
        layout (location = 0) in vec4 vPosition;
        layout (location = 1) in vec4 aColor;
        out vec4 vColor;
        void main()
        {       
           gl_Position = vPosition;
           gl_PointSize = 10.0;
           vColor = aColor;
        }
        """
        val fShaderStr = """#version 300 es
        precision mediump float;
        in vec4 vColor;
        out vec4 fragColor;
        void main()
        {
          fragColor = vColor;
        }
        """
        val linked = IntArray(1)

        // Load the vertex/fragment shaders
        val vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vShaderStr)
        val fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fShaderStr)

        // Create the program object
        val programObject = GLES30.glCreateProgram()
        if (programObject == 0) {
            return
        }
        //将顶点着色器加入到程序
        GLES30.glAttachShader(programObject, vertexShader)
        //将片元着色器加入到程序中
        GLES30.glAttachShader(programObject, fragmentShader)

        // Bind vPosition to attribute 0
        //将vPosition变量与输入属性位置0绑定
        GLES30.glBindAttribLocation(programObject, 0, "vPosition")

        // Link the program
        GLES30.glLinkProgram(programObject)

        // Check the link status
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0)
        if (linked[0] == 0) {
            Log.e(TAG, "Error linking program:")
            Log.e(TAG, GLES30.glGetProgramInfoLog(programObject))
            GLES30.glDeleteProgram(programObject)
            return
        }

        // Store the program object
        mProgramObject = programObject
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 0.0f)
    }

    // /
    // Draw a triangle using the shader pair created in onSurfaceCreated()
    //
    override fun onDrawFrame(glUnused: GL10?) {

        // Clear the color buffer
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        // Use the program object
        GLES30.glUseProgram(mProgramObject)

        //准备坐标数据
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, mVertices)
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(0)

        //绘制三角形颜色
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 0, mColorBuf)

        //绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3)
        //绘制三个点
        //GLES30.glDrawArrays(GLES30.GL_POINTS, 0, 3)
        //绘制直线
        /*GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, 2)
        GLES30.glLineWidth(10f);*/

        GLES30.glDisableVertexAttribArray(0)
        GLES30.glDisableVertexAttribArray(1)
    }

    // /
    // Handle surface changes
    //
    override fun onSurfaceChanged(glUnused: GL10?, width: Int, height: Int) {
        // Set the viewport
        GLES30.glViewport(0, 0, width, height)
    }
}