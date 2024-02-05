package com.example.openglesdemo1.ui.camera.t10

import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLExt
import android.opengl.EGLSurface
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.Matrix
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import com.example.openglesdemo1.utils.TextureUtils
import kotlinx.coroutines.delay
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class SvRenderer(val threadName: String, val width: Int, val height: Int) {

    private val TAG = "SvRenderer"
    private var mEglDisplay: EGLDisplay? = null
    private var mEglContext: EGLContext? = null
    private var mEglSurface: EGLSurface? = null
    private val mHandlerThread by lazy { HandlerThread(threadName) }
    private val mHandler by lazy {
        mHandlerThread.start()
        Handler(mHandlerThread.looper)
    }

    private var mVertexTextureBuffer: FloatBuffer
    private var mIndexBuffer: IntBuffer

    private var mProgramId = 0
    private var mVertexPositionLocation = 0
    private var mTexturePositionLocation = 0
    private var mTextureLocation = 0
    private var mMatrixLocation = 0
    private val mMatrixArray = FloatArray(16)

    //camera纹理需要旋转
    private val mVertexTextureArray = floatArrayOf(
        -1f, -1f, 1f, 1f,
        -1f, 1f, 0f, 1f,
        1f, 1f, 0f, 0f,
        1f, -1f, 1f, 0f
    )

    private val mIndexArray = intArrayOf(
        0, 1, 2, 0, 2, 3
    )

    private var mVboId = 0
    private var mIboId = 0

    init {
        mVertexTextureBuffer =
            ByteBuffer.allocateDirect(mVertexTextureArray.size * java.lang.Float.SIZE / 8)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexTextureBuffer.put(mVertexTextureArray).position(0)
        mIndexBuffer = ByteBuffer.allocateDirect(mIndexArray.size * java.lang.Integer.SIZE / 8)
            .order(ByteOrder.nativeOrder()).asIntBuffer()
        mIndexBuffer.put(mIndexArray).position(0)
    }

    suspend fun getEglContext(): EGLContext? {
        var wait = 0
        var context: EGLContext? = null
        while (wait++ < 10 && null == context) {
            mEglContext?.let {
                context = it
            } ?: delay(100)
        }
        return context
    }

    fun initEgl(sv: Surface, sharedEGLContext: EGLContext?) {
        mHandler.post {
            Log.d(TAG, "initEgl $threadName")
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
                mEglDisplay, eglConfig[0], sharedEGLContext ?: EGL14.EGL_NO_CONTEXT,
                intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 3, EGL14.EGL_NONE),
                0
            )
            mEglSurface = EGL14.eglCreateWindowSurface(
                mEglDisplay, eglConfig[0],
                sv, intArrayOf(EGL14.EGL_NONE), 0
            )

            EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)
            val vertexId =
                ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.mediacodec_t2_vertex))
            val fragmentId =
                ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.mediacodec_t2_fragment))
            mProgramId = ShaderUtils.linkProgram(vertexId, fragmentId)

            GLES30.glUseProgram(mProgramId)
            GLES30.glClearColor(1f, 1f, 1f, 1f)
            mVertexPositionLocation = GLES30.glGetAttribLocation(mProgramId, "position")
            mTexturePositionLocation = GLES30.glGetAttribLocation(mProgramId, "texturePositionIn")
            mTextureLocation = GLES30.glGetUniformLocation(mProgramId, "img")
            mMatrixLocation = GLES30.glGetUniformLocation(mProgramId, "matrix")
            Matrix.orthoM(mMatrixArray, 0, -1f, 1f, -1f, 1f, -1f, 1f)

            val buffer = IntArray(2)
            GLES30.glGenBuffers(buffer.size, buffer, 0)
            mVboId = buffer[0]
            mIboId = buffer[1]

            //vbo
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboId)
            GLES30.glBufferData(
                GLES30.GL_ARRAY_BUFFER,
                mVertexTextureBuffer.capacity() * java.lang.Float.SIZE / 8,
                mVertexTextureBuffer.position(0),
                GLES30.GL_STATIC_DRAW
            )
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)

            //ibo
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mIboId)
            GLES30.glBufferData(
                GLES30.GL_ELEMENT_ARRAY_BUFFER,
                mIndexBuffer.capacity() * Integer.SIZE / 8,
                mIndexBuffer, GLES30.GL_STATIC_DRAW
            )
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)

            GLES30.glUseProgram(0)
        }
    }

    fun changeView(w: Int, h: Int) {
        mHandler.post {
            Log.d(TAG, "changeView $threadName")
            EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)
            GLES30.glUseProgram(mProgramId)
            GLES30.glViewport(0, 0, w, h)
            GLES30.glUseProgram(0)
        }
    }

    fun createSv(f: ((id: Int) -> Unit)) {
        mHandler.post {
            Log.d(TAG, "createSv $threadName")
            EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)
            GLES30.glUseProgram(mProgramId)
            val cameraTextureId = TextureUtils.loadOESTexture()
            f.invoke(cameraTextureId)
            GLES30.glUseProgram(0)
        }
    }

    fun close() {
        mHandlerThread.quitSafely()
    }

    fun draw(textureId: Int, sv: SurfaceTexture?, f: (() -> Unit)?) {
        mHandler.post {
            EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)
            GLES30.glUseProgram(mProgramId)
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

            runCatching {
                sv?.updateTexImage()
            }

            GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
            GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
            GLES30.glUniform1i(mTextureLocation, 0)
            GLES30.glUniformMatrix4fv(mMatrixLocation, 1, false, mMatrixArray, 0)

            GLES30.glEnableVertexAttribArray(mVertexPositionLocation)
            GLES30.glEnableVertexAttribArray(mTexturePositionLocation)
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboId)
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mIboId)
            GLES30.glVertexAttribPointer(
                mVertexPositionLocation, 2, GLES30.GL_FLOAT,
                false, 16, 0
            )
            GLES30.glVertexAttribPointer(
                mTexturePositionLocation, 2, GLES30.GL_FLOAT,
                false, 16, 2 * java.lang.Float.SIZE / 8
            )
            GLES30.glDrawElements(
                GLES30.GL_TRIANGLES, mIndexArray.size, GLES30.GL_UNSIGNED_INT, 0
            )
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)
            GLES30.glDisableVertexAttribArray(mVertexPositionLocation)
            GLES30.glDisableVertexAttribArray(mTexturePositionLocation)

            //交换缓冲区，将刚刚渲染的surface切换到屏幕
            EGL14.eglSwapBuffers(mEglDisplay, mEglSurface)
            f?.invoke()
        }
    }
}