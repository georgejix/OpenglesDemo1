package com.example.openglesdemo1.ui.mediacodec.t4

import android.media.MediaCodec
import android.media.MediaFormat
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import com.example.openglesdemo1.ui.mediacodec.t3.MediaParse
import com.example.openglesdemo1.ui.mediacodec.t3.VideoDecoder
import com.example.openglesdemo1.utils.getInputVideoPath
import kotlinx.android.synthetic.main.activity_mc_and_gl_play_video.gl_sv

class MCAndGlPlayVideoActivity : BaseActivity2() {
    private val mVideoPath by lazy { getInputVideoPath() }
    private var mVideoDecoder: VideoDecoder? = null
    private var mMediaParse: MediaParse? = null
    private var mVideoTrackIndex = 0
    private var mRenderer: VideoRenderer? = null
    private val mHandlerThread by lazy { HandlerThread("back") }
    private val mHandler by lazy {
        mHandlerThread.start()
        Handler(mHandlerThread.looper)
    }
    private val TAG = "MCAndGlPlayVideo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mc_and_gl_play_video)
        mRenderer = VideoRenderer() { play() }
        gl_sv.setEGLContextClientVersion(3)
        gl_sv.setRenderer(mRenderer)
        gl_sv.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    override fun onResume() {
        super.onResume()
        mMediaParse = MediaParse(mVideoPath)
        mVideoDecoder = VideoDecoder()
        mVideoTrackIndex = mMediaParse?.getVideoTrackIndex() ?: 0
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
                    mRenderer?.getSurface(),
                    it,
                    object : MediaCodec.Callback() {
                        override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                            //Log.d(TAG, "onInputBufferAvailable")
                            mHandler.post {
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
                        }

                        override fun onOutputBufferAvailable(
                            codec: MediaCodec,
                            index: Int,
                            info: MediaCodec.BufferInfo
                        ) {
                            //Log.d(TAG, "onOutputBufferAvailable")
                            runCatching {
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