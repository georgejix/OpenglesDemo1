package com.example.openglesdemo1.ui.mediacodec.t2

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

class VideoMuxer(val mPath: String, val width: Int, val height: Int) {
    private val TAG = "VideoMuxer"
    private val mHandlerThread by lazy { HandlerThread("muxer") }
    private val mHandler by lazy {
        mHandlerThread.start()
        Handler(mHandlerThread.looper)
    }
    private var mMediaMuxer: MediaMuxer? = null
    private val INVALID_INDEX = -1
    private var mVideoTrackId = INVALID_INDEX
    private var mVideoEncoder: VideoEncoder? = null
    private val mStop = AtomicBoolean(true)

    fun getSurface() = mVideoEncoder?.getSurface()

    fun createMuxer(f: (() -> Unit)) {
        mHandler.post {
            mMediaMuxer = MediaMuxer(mPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            mVideoEncoder = VideoEncoder(width, height)
            mVideoEncoder?.createEncoder(object : MediaCodec.Callback() {
                override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                    Log.d(TAG, "onInputBufferAvailable")
                }

                override fun onOutputBufferAvailable(
                    codec: MediaCodec,
                    index: Int,
                    info: MediaCodec.BufferInfo
                ) {
                    if (!mStop.get()) {
                        muxerVideo(index, info)
                    }
                }

                override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
                    Log.d(TAG, "onError")
                }

                override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                    Log.d(TAG, "onOutputFormatChanged ${Thread.currentThread().name}")
                    start(format)
                }
            })
            f()
        }
    }

    fun start(format: MediaFormat) {
        mVideoTrackId = mMediaMuxer?.addTrack(format) ?: INVALID_INDEX
        mMediaMuxer?.start()
        mStop.set(false)
    }

    private fun muxerVideo(index: Int, info: MediaCodec.BufferInfo) {
        var buffer = mVideoEncoder?.getEncoder()?.getOutputBuffer(index)

        buffer?.let {
            if (info.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                info.size = 0
                Log.d(TAG, "Ignoring BUFFER_FLAG_CODEC_CONFIG")
            }
            val eos = info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
            if (0 == info.size && !eos) {
                Log.d(TAG, "info.size == 0, drop it.")
                buffer = null
            } else {
            }
            buffer?.let {
                it.position(info.offset)
                it.limit(info.offset + info.size)
                mMediaMuxer?.writeSampleData(mVideoTrackId, it, info)
            }
        }

        mVideoEncoder?.getEncoder()?.releaseOutputBuffer(index, false)
    }

    fun stop() {
        mStop.set(true)
        val bufferInfo = MediaCodec.BufferInfo()
        val buffer = ByteBuffer.allocate(0)
        bufferInfo.set(0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
        if (INVALID_INDEX != mVideoTrackId) {
            mMediaMuxer?.writeSampleData(mVideoTrackId, buffer, bufferInfo)
            mVideoTrackId = INVALID_INDEX
        }
        mVideoEncoder?.stop()
        mMediaMuxer?.stop()
    }

    fun release() {
        mHandlerThread.quitSafely()
        mVideoEncoder?.release()
        mVideoEncoder = null

        mMediaMuxer?.release()
        mMediaMuxer = null
    }
}