package com.example.openglesdemo1.ui.mediacodec.t2

import android.media.MediaCodecInfo
import android.media.MediaFormat
import com.example.openglesdemo1.ui.camera.t6.encoder.MediaEncoder

class LocalVideoEncoder(val width: Int, val height: Int, val muxer: LocalVideoMuxer?) : Thread() {

    private var mEncoder: MediaEncoder? = null
    private var mFormat: MediaFormat? = null

    init {
        mFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        mFormat?.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        mFormat?.setInteger(MediaFormat.KEY_BIT_RATE, 200 * 1000)
        mFormat?.setInteger(MediaFormat.KEY_FRAME_RATE, 15)
        mFormat?.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5)
    }

    fun getFormat() = mFormat

    fun init() {
        muxer?.setVideoEnCodec(this)
        mEncoder
    }

    override fun run() {
        super.run()
    }
}