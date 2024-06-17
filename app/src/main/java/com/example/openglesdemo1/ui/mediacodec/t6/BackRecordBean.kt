package com.example.openglesdemo1.ui.mediacodec.t6

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import android.view.Surface

data class BackRecordBean(
    var mMediaFormat: MediaFormat? = null,
    var mMediaCodec: MediaCodec? = null,
    var mSurface: Surface? = null,
    var mMediaMuxer: MediaMuxer? = null,
    var mVideoTrack: Int? = null,
    var mWriteHead: Boolean = false,
    var mWriteEnd: Boolean = false
)