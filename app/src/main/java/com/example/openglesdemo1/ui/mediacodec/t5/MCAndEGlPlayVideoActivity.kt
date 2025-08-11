package com.example.openglesdemo1.ui.mediacodec.t5

import android.graphics.SurfaceTexture
import android.media.MediaCodec
import android.media.MediaFormat
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import com.example.openglesdemo1.ui.mediacodec.t3.MediaParse
import com.example.openglesdemo1.ui.mediacodec.t3.VideoDecoder
import com.example.openglesdemo1.utils.getInputVideoPath
import kotlinx.android.synthetic.main.activity_mc_and_egl_play_video.sv

class MCAndEGlPlayVideoActivity : BaseActivity2() {
    private val mVideoPath by lazy { getInputVideoPath() }
    private var mVideoDecoder: VideoDecoder? = null
    private var mMediaParse: MediaParse? = null
    private var mVideoTrackIndex = 0
    private var mEglRenderer: EglRenderer? = null
    private var mTextureId = -1
    private var mSurfaceTexture: SurfaceTexture? = null
    private val mHandlerThread by lazy { HandlerThread("back") }
    private val mHandler by lazy {
        mHandlerThread.start()
        Handler(mHandlerThread.looper)
    }
    private val TAG = "MCAndEGlPlayVideo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mc_and_egl_play_video)
        sv.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                mEglRenderer?.initEgl(holder.surface)
                mEglRenderer?.createSv {
                    mTextureId = it
                    mSurfaceTexture = SurfaceTexture(it)
                    play()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder, format: Int,
                width: Int, height: Int
            ) {
                mEglRenderer?.changeView(width, height)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }

        })
    }

    override fun onResume() {
        super.onResume()
        mMediaParse = MediaParse(mVideoPath)
        mVideoDecoder = VideoDecoder()
        mVideoTrackIndex = mMediaParse?.getVideoTrackIndex() ?: 0
        mEglRenderer = EglRenderer("back2")
    }

    override fun onPause() {
        super.onPause()
        mVideoDecoder?.stop()
        mVideoDecoder = null
        mMediaParse?.stop()
        mMediaParse = null
    }

    private fun play() {
        mMediaParse?.getVideoFormat()?.let {
            mHandler.post {
                mVideoDecoder?.initDecoder(
                    Surface(mSurfaceTexture),
                    it,
                    object : MediaCodec.Callback() {
                        override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                            //Log.d(TAG, "onInputBufferAvailable")
                            runCatching {
                                codec.getInputBuffer(index)?.let {
                                    val result =
                                        mMediaParse?.readBuffer(mVideoTrackIndex, it) ?: -1
                                    Log.d(
                                        TAG,
                                        "read size =$result delay=${mMediaParse?.getDelayTime()}"
                                    )
                                    if (result > 0) {
                                        Thread.sleep(mMediaParse?.getDelayTime() ?: 0)
                                        Log.d(
                                            TAG,
                                            "time = ${mMediaParse?.getCurrentTimestamp()}"
                                        )
                                        mVideoDecoder?.mMediaCodec?.queueInputBuffer(
                                            index,
                                            0,
                                            result,
                                            mMediaParse?.getCurrentTimestamp() ?: 0,
                                            0
                                        )
                                    } else {
                                        mVideoDecoder?.mMediaCodec?.queueInputBuffer(
                                            index, 0, 0, 0,
                                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                                        )
                                    }
                                }
                            }
                        }

                        override fun onOutputBufferAvailable(
                            codec: MediaCodec,
                            index: Int,
                            info: MediaCodec.BufferInfo
                        ) {
                            //Log.d(TAG, "onOutputBufferAvailable")
                            runCatching {
                                mEglRenderer?.draw(mTextureId, mSurfaceTexture)
                                mVideoDecoder?.mMediaCodec?.releaseOutputBuffer(index, true)
                            }
                        }

                        override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
                            //Log.d(TAG, "onError")
                        }

                        override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                            //Log.d(TAG, "onOutputFormatChanged")
                        }
                    })
            }
        }
    }
}