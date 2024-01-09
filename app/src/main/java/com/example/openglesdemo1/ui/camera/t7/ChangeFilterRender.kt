package com.example.openglesdemo1.ui.camera.t7

import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import java.io.BufferedOutputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ChangeFilterRender(val mContext: Context, val mListener: Listener?) : GLSurfaceView.Renderer {

    private val mCameraFilter: CameraFilter by lazy { CameraFilter() }
    private val mColorFilter: ColorFilter by lazy { ColorFilter(mContext) }
    private var mTakePhotoAtomic = AtomicBoolean(false)
    private var mWidth = 0
    private var mHeight = 0
    private var mTakePhotoFrameBuffer = IntArray(1)
    private var mTakePhotoFrameTextureId = IntArray(1)

    fun setMatrix(back: Boolean, p: Float) {
        mCameraFilter.setMatrix(back, p)
        mColorFilter.setMatrix(back, p)
    }

    fun changeFilter() {
        mColorFilter.changeFilter()
    }

    fun takePhoto() {
        mTakePhotoAtomic.set(true)
    }

    fun getSurfaceTexture(): SurfaceTexture? = mCameraFilter.mCameraSurfaceTexture

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d(ChangeFilterActivity.TAG, "onSurfaceCreated")
        mCameraFilter.onSurfaceCreate()
        mColorFilter.onSurfaceCreate()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.d(ChangeFilterActivity.TAG, "onSurfaceChanged")
        mCameraFilter.onSurfaceChanged(width, height)
        mColorFilter.onSurfaceChanged(width, height)
        mListener?.onOpenCamera()
        mWidth = width
        mHeight = height

        GLES30.glDeleteFramebuffers(mTakePhotoFrameBuffer.size, mTakePhotoFrameBuffer, 0)
        GLES30.glDeleteTextures(mTakePhotoFrameTextureId.size, mTakePhotoFrameTextureId, 0)
        GLES30.glGenFramebuffers(mTakePhotoFrameBuffer.size, mTakePhotoFrameBuffer, 0)
        GLES30.glGenTextures(mTakePhotoFrameTextureId.size, mTakePhotoFrameTextureId, 0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTakePhotoFrameTextureId[0])
        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height, 0, GLES30.GL_RGBA,
            GLES30.GL_UNSIGNED_BYTE, null
        )
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_WRAP_S,
            GLES30.GL_CLAMP_TO_EDGE
        )
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_WRAP_T,
            GLES30.GL_CLAMP_TO_EDGE
        )
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        mCameraFilter.onDraw()
        mColorFilter.mColorTextureId = mCameraFilter.mFrameTextureId
        if (mTakePhotoAtomic.getAndSet(false)) {
            val byteBuffer = ByteBuffer.allocate(4 * mWidth * mHeight)
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mTakePhotoFrameBuffer[0])
            GLES30.glFramebufferTexture2D(
                GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_TEXTURE_2D, mTakePhotoFrameTextureId[0], 0
            )
            mColorFilter.transYMatrix()
            mColorFilter.onDraw()
            GLES30.glReadPixels(
                0, 0, mWidth, mHeight,
                GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, byteBuffer
            )
            savePhoto(byteBuffer)
            mColorFilter.transYMatrix()
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
        } else {
            mColorFilter.onDraw()
        }
    }

    private fun savePhoto(buffer: ByteBuffer) {
        Thread {
            val bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
            bitmap.copyPixelsFromBuffer(buffer)
            val filePath =
                "${mContext.getExternalFilesDir("image")?.absolutePath}/${System.currentTimeMillis()}.jpg"
            Log.d(ChangeFilterActivity.TAG, "${filePath}")
            var bos: BufferedOutputStream? = null
            try {
                val fos = FileOutputStream(filePath)
                bos = BufferedOutputStream(fos)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                try {
                    bos?.flush()
                    bos?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                bitmap?.recycle()
            }
        }.start()
    }

    interface Listener {
        fun onOpenCamera()
    }
}