package com.example.openglesdemo1.ui.t10

import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLSurface
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.view.TextureView

class TextureEGLHelper : HandlerThread("TextureEGLHelper"),
    SurfaceTexture.OnFrameAvailableListener {

    private var mHandlerThread: HandlerThread? = null
    private var mHandler: Handler? = null
    private var mTextureView: TextureView? = null
    private var mOESTextureId = 0//纹理ID
    private var mEGLDisplay = EGL14.EGL_NO_DISPLAY//显示设备
    private var mEGLContext: EGLContext? = null//EGL上下文
    private var mConfigs = Array<EGLConfig?>(1) { _ -> null }//描述帧缓冲区配置参数
    private var mEglSurface: EGLSurface? = null//EGL绘图表面

    /**
     * 自定义的SurfaceTexture
     * 用来接受Camera数据作二次处理
     */
    private var mOESSurfaceTexture: SurfaceTexture? = null
    private var mTextureRenderer: CameraTextureRender? = null

    object EGLMessage {
        val MSG_INIT = 1
        val MSG_RENDER = 2
        val MSG_DESTROY = 3
    }

    inner class TextureHandler(looper: Looper?) : Handler(looper!!) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                EGLMessage.MSG_INIT -> {
                    initEGLContext(3)
                    return
                }
                EGLMessage.MSG_RENDER -> {
                    drawFrame()
                    return
                }
                EGLMessage.MSG_DESTROY -> return
                else -> return
            }
        }
    }

    fun initEgl(textureView: TextureView, textureId: Int) {
        mTextureView = textureView
        mOESTextureId = textureId
        mHandlerThread = HandlerThread("Renderer Thread")
        mHandlerThread?.start()
        mHandler = TextureHandler(mHandlerThread!!.looper)
        mHandler?.sendEmptyMessage(EGLMessage.MSG_INIT)
    }

    private fun initEGLContext(clientVersion: Int) {
        //获取显示设备
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        if (mEGLDisplay === EGL14.EGL_NO_DISPLAY) {
            throw RuntimeException("eglGetDisplay error: " + EGL14.eglGetError())
        }
        //存放EGL版本号
        val version = IntArray(2)
        version[0] = 3
        if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
            throw RuntimeException("eglInitialize error: " + EGL14.eglGetError())
        }
        //配置列表
        val attributes = intArrayOf(
            EGL14.EGL_BUFFER_SIZE, 32,
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL14.EGL_RENDERABLE_TYPE, 4,
            EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT,
            EGL14.EGL_NONE
        )
        val numConfigs = IntArray(1)
        //EGL选择配置
        if (!EGL14.eglChooseConfig(
                mEGLDisplay,
                attributes,
                0,
                mConfigs,
                0,
                mConfigs.size,
                numConfigs,
                0
            )
        ) {
            throw RuntimeException("eglChooseConfig error: " + EGL14.eglGetError())
        }
        val surfaceTexture = mTextureView!!.surfaceTexture
            ?: throw RuntimeException("surfaceTexture is null")
        //创建EGL显示窗口
        val surfaceAttributes = intArrayOf(EGL14.EGL_NONE)
        mEglSurface = EGL14.eglCreateWindowSurface(
            mEGLDisplay,
            mConfigs.get(0),
            surfaceTexture,
            surfaceAttributes,
            0
        )
        //创建上下文环境
        val contextAttributes = intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION, clientVersion,
            EGL14.EGL_NONE
        )
        mEGLContext = EGL14.eglCreateContext(
            mEGLDisplay,
            mConfigs.get(0),
            EGL14.EGL_NO_CONTEXT,
            contextAttributes,
            0
        )
        if (mEGLDisplay === EGL14.EGL_NO_DISPLAY || mEGLContext === EGL14.EGL_NO_CONTEXT) {
            throw RuntimeException("eglCreateContext fail error: " + EGL14.eglGetError())
        }
        if (!EGL14.eglMakeCurrent(mEGLDisplay, mEglSurface, mEglSurface, mEGLContext)) {
            throw RuntimeException("eglMakeCurrent error: " + EGL14.eglGetError())
        }
        //加载渲染器
        mTextureRenderer = CameraTextureRender(mOESTextureId)
        mTextureRenderer?.onSurfaceCreated()
    }

    fun onSurfaceChanged(width: Int, height: Int) {
        mTextureRenderer?.onSurfaceChanged(width, height)
    }

    private fun drawFrame() {
        mTextureRenderer?.let {
            EGL14.eglMakeCurrent(mEGLDisplay, mEglSurface, mEglSurface, mEGLContext)
            mTextureRenderer?.onDrawFrame(mOESSurfaceTexture)
            EGL14.eglSwapBuffers(mEGLDisplay, mEglSurface)
        }
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        mHandler?.sendEmptyMessage(EGLMessage.MSG_RENDER)
    }

    fun loadOESTexture(): SurfaceTexture? {
        mOESSurfaceTexture = SurfaceTexture(mOESTextureId)
        mOESSurfaceTexture?.setOnFrameAvailableListener(this)
        return mOESSurfaceTexture
    }

    /**
     * 销毁
     * 释放
     */
    fun onDestroy() {
        mHandlerThread?.quitSafely()
        mHandler?.removeCallbacksAndMessages(null)
    }
}