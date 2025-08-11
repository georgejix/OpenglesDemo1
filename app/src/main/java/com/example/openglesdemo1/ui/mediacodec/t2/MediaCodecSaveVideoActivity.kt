package com.example.openglesdemo1.ui.mediacodec.t2

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import com.example.openglesdemo1.utils.getOutputVideoPath
import kotlinx.android.synthetic.main.activity_mediacodec_save_video.btn_record
import kotlinx.android.synthetic.main.activity_mediacodec_save_video.sv1
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediaCodecSaveVideoActivity : BaseActivity2() {
    private val TAG = "MediaCodecSaveVideoActivity"
    private val WIDTH = 1080
    private val HEIGHT = 1920
    private var mTId = -1
    private var mSv: SurfaceTexture? = null
    private var mRender1: SvRenderer? = null
    private var mRender2: SvRenderer? = null
    private var mVideoMuxer: VideoMuxer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mediacodec_save_video)
        sv1.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                initEgl()
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
        setListener()
    }

    private fun setListener() {
        btn_record.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mVideoMuxer = VideoMuxer(getOutputVideoPath(), 1080, 1920)
                mVideoMuxer?.createMuxer() {
                    initMuxerEgl()
                }
            } else {
                mVideoMuxer?.stop()
                mVideoMuxer?.release()
            }
        }
    }

    private fun initEgl() {
        CoroutineScope(Dispatchers.Main).launch {
            mRender1 = SvRenderer("sv1", WIDTH, HEIGHT)
            mRender1?.initEgl(sv1.holder.surface, null)

            mRender1?.createSv { id ->
                CoroutineScope(Dispatchers.Main).launch {
                    val sv = SurfaceTexture(id)
                    sv.setDefaultBufferSize(HEIGHT, WIDTH)
                    sv.setOnFrameAvailableListener {
                        mRender1?.draw(mTId, mSv)
                        mRender2?.draw(mTId, null)
                    }
                    openCamera(listOf(Surface(sv)))
                    mTId = id
                    mSv = sv
                }
            }
        }
    }

    private fun initMuxerEgl() {
        CoroutineScope(Dispatchers.Main).launch {
            mVideoMuxer?.getSurface()?.let {
                mRender2 = SvRenderer("sv2", WIDTH, HEIGHT)
                mRender2?.initEgl(it, mRender1?.getEglContext())
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