package com.example.openglesdemo1.ui.mediacodec.t2

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.view.Surface

class VideoEncoder(val width: Int, val height: Int) {
    private var mMediaCodec: MediaCodec? = null
    private var mMediaFormat: MediaFormat? = null
    private var mSurface: Surface? = null

    fun createEncoder(callback: MediaCodec.Callback) {
        val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        format.setInteger(MediaFormat.KEY_BIT_RATE, 2000 * 1000)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 15)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5)
        mMediaFormat = format

        mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        mMediaCodec?.setCallback(callback)
        mMediaCodec?.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mSurface = mMediaCodec?.createInputSurface()
        mMediaCodec?.start()
    }

    fun getEncoder() = mMediaCodec

    fun getSurface() = mSurface

    fun stop() {
        mMediaCodec?.stop()
    }

    fun release() {
        mSurface?.release()
        mMediaCodec?.release()
    }

}