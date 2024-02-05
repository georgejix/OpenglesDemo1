package com.example.openglesdemo1.ui.normal.t16

import android.app.Activity
import android.opengl.*
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import com.example.openglesdemo1.utils.TextureUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class EglActivity : Activity() {
    private var eglDisplay = EGL14.EGL_NO_DISPLAY
    private var eglSurface = EGL14.EGL_NO_SURFACE
    private var eglContext = EGL14.EGL_NO_CONTEXT
    private val mSurfaceV: SurfaceView by lazy { SurfaceView(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSurfaceV.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                initEgl()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                Matrix.orthoM(
                    mMatrixArray, 0, -1f, 1f,
                    -1f * height / width, 1f * height / width, -1f, 1f
                )
                draw()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }

        })
        setContentView(mSurfaceV)
    }

    private fun initEgl() {
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        val version = IntArray(2)
        EGL14.eglInitialize(eglDisplay, version, 0, version, 1)
        val attrList = intArrayOf(
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT or EGLExt.EGL_OPENGL_ES3_BIT_KHR,
            EGL14.EGL_NONE
        )
        val eglConfig = arrayOfNulls<EGLConfig>(1)
        val configNum = IntArray(1)
        EGL14.eglChooseConfig(
            eglDisplay, attrList, 0, eglConfig, 0,
            eglConfig.size, configNum, 0
        )
        eglContext = EGL14.eglCreateContext(
            eglDisplay,
            eglConfig[0],
            EGL14.EGL_NO_CONTEXT,
            intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 3, EGL14.EGL_NONE),
            0
        )
        eglSurface = EGL14.eglCreateWindowSurface(
            eglDisplay, eglConfig[0], mSurfaceV,
            intArrayOf(EGL14.EGL_NONE), 0
        )
    }

    private var mVertexTextureBuffer: FloatBuffer
    private var mIndexBuffer: IntBuffer

    private var mProgramId = 0
    private var mTextureId = 0
    private var mVertexPositionLocation = 0
    private var mTexturePositionLocation = 0
    private var mTextureLocation = 0
    private var mMatrixLocation = 0
    private val mMatrixArray = FloatArray(16)

    private val mVertexTextureArray = floatArrayOf(
        -1f, -1f, 0f, 1f,
        -1f, 1f, 0f, 0f,
        1f, 1f, 1f, 0f,
        1f, -1f, 1f, 1f
    )

    private val mIndexArray = intArrayOf(
        0, 1, 2, 0, 2, 3
    )

    init {
        mVertexTextureBuffer =
            ByteBuffer.allocateDirect(mVertexTextureArray.size * java.lang.Float.SIZE / 8)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexTextureBuffer.put(mVertexTextureArray).position(0)
        mIndexBuffer = ByteBuffer.allocateDirect(mIndexArray.size * java.lang.Integer.SIZE / 8)
            .order(ByteOrder.nativeOrder()).asIntBuffer()
        mIndexBuffer.put(mIndexArray).position(0)
    }

    private fun draw() {
        EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)

        val vertexId =
            ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.normal_t16_vertex))
        val fragmentId =
            ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.normal_t16_fragment))
        mProgramId = ShaderUtils.linkProgram(vertexId, fragmentId)

        GLES30.glUseProgram(mProgramId)
        mVertexPositionLocation = GLES30.glGetAttribLocation(mProgramId, "vertexPosition")
        mTexturePositionLocation = GLES30.glGetAttribLocation(mProgramId, "texturePosition")
        mTextureLocation = GLES30.glGetUniformLocation(mProgramId, "texture")
        mMatrixLocation = GLES30.glGetUniformLocation(mProgramId, "matrix")

        mTextureId = TextureUtils.loadTexture(this, R.mipmap.main)
        GLES30.glEnableVertexAttribArray(mVertexPositionLocation)
        GLES30.glVertexAttribPointer(
            mVertexPositionLocation, 2, GLES30.GL_FLOAT,
            false, 16, mVertexTextureBuffer
        )
        GLES30.glEnableVertexAttribArray(mTexturePositionLocation)
        GLES30.glVertexAttribPointer(
            mTexturePositionLocation, 2, GLES30.GL_FLOAT,
            false, 16, mVertexTextureBuffer.position(2)
        )
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId)
        GLES30.glUniform1i(mTextureLocation, 0)
        GLES30.glUniformMatrix4fv(mMatrixLocation, 1, false, mMatrixArray, 0)

        GLES30.glClearColor(1f, 1f, 1f, 1f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glDrawElements(
            GLES30.GL_TRIANGLES, mIndexArray.size, GLES30.GL_UNSIGNED_INT, mIndexBuffer
        )

        //交换缓冲区，将刚刚渲染的surface切换到屏幕
        EGL14.eglSwapBuffers(eglDisplay, eglSurface)
    }
}