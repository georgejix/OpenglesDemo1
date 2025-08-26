package com.example.openglesdemo1.ui.camera.t12

import android.opengl.EGL14
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLExt
import android.opengl.EGLSurface
import android.opengl.GLES30
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class PreviewRender(val renderName: String) {
    private val TAG = javaClass.simpleName
    private var mEglDisplay: EGLDisplay? = null
    private var mEglContext: EGLContext? = null
    private var mEglSurface: EGLSurface? = null
    private val mHandlerThread by lazy { HandlerThread(renderName) }
    private val mHandler by lazy {
        mHandlerThread.start()
        Handler(mHandlerThread.looper)
    }
    private val mVertexTextureArray = floatArrayOf(
        -1f, -1f, 0f, 0f,
        -1f, 1f, 0f, 1f,
        1f, 1f, 1f, 1f,
        1f, -1f, 1f, 0f
    )

    private val mIndexArray = intArrayOf(0, 1, 2, 0, 2, 3)
    private val mVertexTextureBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(mVertexTextureArray.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().also {
                it.put(mVertexTextureArray).position(0)
            }
    }
    private val mIndexBuffer: IntBuffer by lazy {
        ByteBuffer.allocateDirect(mIndexArray.size * 4)
            .order(ByteOrder.nativeOrder()).asIntBuffer().also {
                it.put(mIndexArray).position(0)
            }
    }
    private var mProgramId = 0
    private var mPositionLocation = 0
    private var mTexturePositionLocation = 0
    private var mImgLocation = 0
    private var mVboId = 0
    private var mIboId = 0

    fun initEgl(sv: Surface, sharedContext: EGLContext) {
        mHandler.post {
            Log.d(TAG, "initEgl $TAG")
            mEglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
            val version = IntArray(2)
            EGL14.eglInitialize(mEglDisplay, version, 0, version, 1)
            val attr = intArrayOf(
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE,
                EGL14.EGL_OPENGL_ES2_BIT or EGLExt.EGL_OPENGL_ES3_BIT_KHR,
                EGL14.EGL_NONE
            )
            val eglConfig = arrayOfNulls<android.opengl.EGLConfig>(1)
            val configNum = IntArray(1)
            EGL14.eglChooseConfig(mEglDisplay, attr, 0, eglConfig, 0, configNum.size, configNum, 0)
            mEglContext = EGL14.eglCreateContext(
                mEglDisplay, eglConfig[0], sharedContext,
                intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 3, EGL14.EGL_NONE), 0
            )
            mEglSurface = EGL14.eglCreateWindowSurface(
                mEglDisplay, eglConfig[0], sv, intArrayOf(EGL14.EGL_NONE), 0
            )

            EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)
            val vertexId =
                ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.camera2_t12_vertex2))
            val fragmentId =
                ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.camera2_t12_fragment2))
            mProgramId = ShaderUtils.linkProgram(vertexId, fragmentId)

            GLES30.glUseProgram(mProgramId)
            GLES30.glClearColor(1f, 1f, 1f, 1f)
            mPositionLocation = GLES30.glGetAttribLocation(mProgramId, "position2")
            mTexturePositionLocation = GLES30.glGetAttribLocation(mProgramId, "texturePositionIn2")
            mImgLocation = GLES30.glGetUniformLocation(mProgramId, "img2")
            val buffer = IntArray(2)
            GLES30.glGenBuffers(buffer.size, buffer, 0)
            mVboId = buffer[0]
            mIboId = buffer[1]

            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboId)
            GLES30.glBufferData(
                GLES30.GL_ARRAY_BUFFER,
                mVertexTextureBuffer.capacity() * 4,
                mVertexTextureBuffer.position(0),
                GLES30.GL_STATIC_DRAW
            )
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)

            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mIboId)
            GLES30.glBufferData(
                GLES30.GL_ELEMENT_ARRAY_BUFFER,
                mIndexBuffer.capacity() * 4,
                mIndexBuffer, GLES30.GL_STATIC_DRAW
            )
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)
            GLES30.glUseProgram(0)
        }
    }

    fun changeView(w: Int, h: Int) {
        mHandler.post {
            Log.d(TAG, "changeView $renderName")
            EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)
            GLES30.glUseProgram(mProgramId)
            GLES30.glViewport(0, 0, w, h)
            GLES30.glUseProgram(0)
        }
    }

    fun close() {
        mHandlerThread.quitSafely()
    }

    fun draw(textureId: Int, fence: Long) {
        mHandler.post {
            GLES30.glWaitSync(fence, 0, GLES30.GL_TIMEOUT_IGNORED)
            GLES30.glDeleteSync(fence)
            Log.d(TAG, "draw $renderName")

            EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)
            GLES30.glUseProgram(mProgramId)
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
            GLES30.glEnableVertexAttribArray(mPositionLocation)
            GLES30.glEnableVertexAttribArray(mTexturePositionLocation)

            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboId)
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mIboId)
            GLES30.glActiveTexture(GLES30.GL_TEXTURE2)
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
            GLES30.glUniform1i(mImgLocation, 2)
            GLES30.glVertexAttribPointer(
                mPositionLocation, 2, GLES30.GL_FLOAT, false, 16, 0
            )
            GLES30.glVertexAttribPointer(
                mTexturePositionLocation, 2, GLES30.GL_FLOAT, false, 16, 8
            )
            GLES30.glDrawElements(
                GLES30.GL_TRIANGLES, mIndexArray.size, GLES30.GL_UNSIGNED_INT, 0
            )
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
            GLES30.glDisableVertexAttribArray(mPositionLocation)
            GLES30.glDisableVertexAttribArray(mTexturePositionLocation)
            EGL14.eglSwapBuffers(mEglDisplay, mEglSurface)
        }
    }
}