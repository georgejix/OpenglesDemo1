package com.example.openglesdemo1.ui.mediacodec.t2

import android.media.MediaCodec
import android.media.MediaMuxer
import java.util.concurrent.atomic.AtomicBoolean

class LocalVideoMuxer(val path: String) {
    private var mMuxer: MediaMuxer? = null
    private val mStart = AtomicBoolean(false)
    private var mVideoEncoder: LocalVideoEncoder? = null

    init {
        mMuxer = MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    }

    fun setVideoEnCodec(enCodec: LocalVideoEncoder?) {
        mVideoEncoder = enCodec
        mVideoEncoder?.getFormat()?.let { mMuxer?.addTrack(it) }
    }

    fun start() {
        mMuxer?.start()
        mVideoEncoder?.start()
        mStart.set(true)
    }

    fun stop() {
        mMuxer?.stop()
        mMuxer?.release()
        mStart.set(false)
    }

    fun isMuxerStart() = mStart.get()
}