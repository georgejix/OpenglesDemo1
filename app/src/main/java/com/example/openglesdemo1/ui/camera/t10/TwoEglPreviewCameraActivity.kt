package com.example.openglesdemo1.ui.camera.t10

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import kotlinx.android.synthetic.main.activity_mediacodec_save_video.sv1
import kotlinx.android.synthetic.main.activity_mediacodec_save_video.sv2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TwoEglPreviewCameraActivity : BaseActivity2() {
    private val TAG = "MediaCodecSaveVideoActivity"
    private val WIDTH = 1080
    private val HEIGHT = 1920
    private var mTId = -1
    private var mSv: SurfaceTexture? = null
    private var mRender1: SvRenderer? = null
    private var mRender2: SvRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two_egl_preview_camera)
        sv1.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                initEgl(1)
            }

            override fun surfaceChanged(
                holder: SurfaceHolder, format: Int,
                width: Int, height: Int
            ) {
                mRender1?.changeView(width, height)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                stopPreview()
            }
        })
        sv2.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                initEgl(2)
            }

            override fun surfaceChanged(
                holder: SurfaceHolder, format: Int,
                width: Int, height: Int
            ) {
                mRender2?.changeView(width, height)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                stopPreview()
            }
        })
    }

    private fun initEgl(index: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            when (index) {
                1 -> {
                    mRender1 = SvRenderer("sv1", WIDTH, HEIGHT)
                    mRender1?.initEgl(sv1.holder.surface, null)
                }

                2 -> {
                    mRender2 = SvRenderer("sv2", WIDTH, HEIGHT)
                    mRender2?.initEgl(sv2.holder.surface, mRender1?.getEglContext())
                }
            }
            if (null != mRender1 && null != mRender2) {
                mRender1?.createSv { id ->
                    CoroutineScope(Dispatchers.Main).launch {
                        val sv = SurfaceTexture(id)
                        sv.setDefaultBufferSize(HEIGHT, WIDTH)
                        sv.setOnFrameAvailableListener {
                            mRender1?.draw(mTId, mSv, null)
                            mRender2?.draw(mTId, null, null)
                        }
                        openCamera(listOf(Surface(sv)))
                        mTId = id
                        mSv = sv
                    }
                }
            }
        }
    }

    private fun stopPreview() {
        mRender1?.close()
        mRender1 = null
        mRender2?.close()
        mRender2 = null
        closeCamera()
    }

    override fun onPause() {
        super.onPause()
        stopPreview()
    }
}