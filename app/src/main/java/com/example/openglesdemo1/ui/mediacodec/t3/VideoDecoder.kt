package com.example.openglesdemo1.ui.mediacodec.t3

import android.media.MediaCodec
import android.media.MediaFormat
import android.view.Surface

class VideoDecoder() {

    var mMediaCodec: MediaCodec? = null

    fun initDecoder(surface: Surface?, codecFormat: MediaFormat, listener: MediaCodec.Callback) {
        mMediaCodec = MediaCodec.createDecoderByType(codecFormat.getString(MediaFormat.KEY_MIME)!!)
        mMediaCodec?.configure(codecFormat, surface, null, 0)
        mMediaCodec?.setCallback(listener)
        mMediaCodec?.start()
    }

    fun stop() {
        mMediaCodec?.stop()
        mMediaCodec?.release()
        mMediaCodec = null
    }
}