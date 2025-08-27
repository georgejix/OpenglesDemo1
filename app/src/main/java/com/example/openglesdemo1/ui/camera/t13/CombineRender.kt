package com.example.openglesdemo1.ui.camera.t13

import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLExt
import android.opengl.EGLSurface
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLUtils
import android.opengl.Matrix
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.example.openglesdemo1.BaseApplication
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import com.example.openglesdemo1.utils.TextureUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class CombineRender(val renderName: String) {
    private val TAG = javaClass.simpleName
    private var mEglDisplay: EGLDisplay? = null
    private var mEglContext: EGLContext? = null
    private var mBufferSurface: EGLSurface? = null
    private val mHandlerThread by lazy { HandlerThread(renderName) }
    private val mHandler by lazy {
        mHandlerThread.start()
        Handler(mHandlerThread.looper)
    }
    private val mVertexTextureArray = floatArrayOf(
        -1f, -1f, 1f, 1f,
        -1f, 1f, 0f, 1f,
        1f, 1f, 0f, 0f,
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
    private var mTopImgLocation = 0
    private var mMatrixLocation = 0
    private val mMatrixArray = FloatArray(16)
    private var mTopImgTextureId = 0
    private var mVboId = 0
    private var mIboId = 0
    private var mImgTextureId = 0
    private var mImgSurfaceTexture: SurfaceTexture? = null
    private var mOutputTextureId = 0
    private var mFrameBufferId = 0

    fun initEgl(f: ((context: EGLContext) -> Unit)) {
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
                mEglDisplay, eglConfig[0], EGL14.EGL_NO_CONTEXT,
                intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 3, EGL14.EGL_NONE), 0
            )
            mBufferSurface =
                EGL14.eglCreatePbufferSurface(mEglDisplay, eglConfig[0], attr, 0)
            EGL14.eglMakeCurrent(mEglDisplay, mBufferSurface, mBufferSurface, mEglContext)
            val vertexId =
                ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.camera2_t13_vertex))
            val fragmentId =
                ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.camera2_t13_fragment))
            mProgramId = ShaderUtils.linkProgram(vertexId, fragmentId)

            GLES30.glUseProgram(mProgramId)
            GLES30.glClearColor(1f, 1f, 1f, 1f)
            mPositionLocation = GLES30.glGetAttribLocation(mProgramId, "position")
            mTexturePositionLocation = GLES30.glGetAttribLocation(mProgramId, "texturePositionIn")
            mImgLocation = GLES30.glGetUniformLocation(mProgramId, "img")
            mTopImgLocation = GLES30.glGetUniformLocation(mProgramId, "topImg")
            mMatrixLocation = GLES30.glGetUniformLocation(mProgramId, "matrix")
            Matrix.orthoM(mMatrixArray, 0, -1f, 1f, -1f, 1f, -1f, 1f)
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
            mTopImgTextureId = TextureUtils.loadTexture(1080, 80)
            f?.invoke(mEglContext!!)
        }
    }

    fun changeView(w: Int, h: Int) {
        mHandler.post {
            Log.d(TAG, "changeView $renderName")
            EGL14.eglMakeCurrent(mEglDisplay, mBufferSurface, mBufferSurface, mEglContext)
            GLES30.glUseProgram(mProgramId)
            GLES30.glViewport(0, 0, w, h)
            val sharedTextures = IntArray(1)
            GLES30.glGenTextures(sharedTextures.size, sharedTextures, 0)
            mOutputTextureId = sharedTextures[0]
            val frameBuffers = IntArray(1)
            GLES30.glGenFramebuffers(frameBuffers.size, frameBuffers, 0)
            mFrameBufferId = frameBuffers[0]
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBufferId)
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mOutputTextureId)
            GLES30.glTexParameteri(
                GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR
            )
            GLES30.glTexParameteri(
                GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR
            )
            GLES30.glTexParameteri(
                GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE
            )
            GLES30.glTexParameteri(
                GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE
            )
            GLES30.glTexImage2D(
                GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, w, h,
                0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null
            )
            GLES30.glFramebufferTexture2D(
                GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_TEXTURE_2D, mOutputTextureId, 0
            )
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
            GLES30.glUseProgram(0)
        }
    }

    fun updateTopImg(bitmap: Bitmap) {
        mHandler.post {
            EGL14.eglMakeCurrent(mEglDisplay, mBufferSurface, mBufferSurface, mEglContext)
            GLES30.glUseProgram(mProgramId)
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTopImgTextureId)
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
            GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)
            bitmap.recycle()
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
            GLES30.glUseProgram(0)
        }
    }

    fun createSv(f: ((st: SurfaceTexture) -> Unit)) {
        mHandler.post {
            Log.d(TAG, "createSv $renderName")
            EGL14.eglMakeCurrent(mEglDisplay, mBufferSurface, mBufferSurface, mEglContext)
            GLES30.glUseProgram(mProgramId)
            mImgTextureId = TextureUtils.loadOESTexture()
            mImgSurfaceTexture = SurfaceTexture(mImgTextureId)
            f.invoke(mImgSurfaceTexture!!)
            GLES30.glUseProgram(0)
        }
    }

    fun close() {
        mHandlerThread.quitSafely()
    }

    fun draw(f: ((tid: Int, fence: Long) -> Unit)?) {
        mHandler.post {
            EGL14.eglMakeCurrent(mEglDisplay, mBufferSurface, mBufferSurface, mEglContext)
            GLES30.glUseProgram(mProgramId)
            //GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
            runCatching { mImgSurfaceTexture?.updateTexImage() }

            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBufferId)
            GLES30.glUniformMatrix4fv(mMatrixLocation, 1, false, mMatrixArray, 0)
            GLES30.glEnableVertexAttribArray(mPositionLocation)
            GLES30.glEnableVertexAttribArray(mTexturePositionLocation)

            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboId)
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mIboId)
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
            GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mImgTextureId)
            GLES30.glUniform1i(mImgLocation, 0)
            GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTopImgTextureId)
            GLES30.glUniform1i(mTopImgLocation, 1)
            GLES30.glVertexAttribPointer(
                mPositionLocation, 2, GLES30.GL_FLOAT, false, 16, 0
            )
            GLES30.glVertexAttribPointer(
                mTexturePositionLocation, 2, GLES30.GL_FLOAT, false, 16, 8
            )
            GLES30.glDrawElements(
                GLES30.GL_TRIANGLES, mIndexArray.size, GLES30.GL_UNSIGNED_INT, 0
            )
            val fenceSyncObject = GLES30.glFenceSync(GLES30.GL_SYNC_GPU_COMMANDS_COMPLETE, 0)
            f?.invoke(mOutputTextureId, fenceSyncObject)
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)
            GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
            GLES30.glDisableVertexAttribArray(mPositionLocation)
            GLES30.glDisableVertexAttribArray(mTexturePositionLocation)
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
        }
    }
}