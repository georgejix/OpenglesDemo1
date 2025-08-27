package com.example.openglesdemo1.ui.camera.t13

import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import android.view.Surface
import com.example.openglesdemo1.utils.getOutputVideoPath
import java.io.File
import java.nio.ByteBuffer

class CodecUtil(val mContext: Context) {
    private val TAG = javaClass.simpleName
    private var mMediaFormat: MediaFormat? = null
    private val mRecordSurface: Surface by lazy { MediaCodec.createPersistentInputSurface() }
    private var mMediaMuxer: MediaMuxer? = null
    private var mVideoTrackId = -1
    private var mMediaCodec: MediaCodec? = null
    private var mPath = ""

    private val mMediaCodecCb = object : MediaCodec.Callback() {
        override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
            Log.d(TAG, "onInputBufferAvailable")
        }

        override fun onOutputBufferAvailable(
            codec: MediaCodec, index: Int, info: MediaCodec.BufferInfo
        ) {
            Log.d(TAG, "onOutputBufferAvailable")
            codec.getOutputBuffer(index)?.let { data ->
                mMediaMuxer?.writeSampleData(mVideoTrackId, data, info)
            }
            codec.releaseOutputBuffer(index, false)
        }

        override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
            Log.d(TAG, "onError")
        }

        override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
            Log.d(TAG, "onOutputFormatChanged")
            mMediaMuxer ?: let {
                mPath = mContext.getOutputVideoPath()
                File(mPath).let {
                    if (it.exists()) {
                        it.delete()
                    }
                    it.createNewFile()
                }
                mMediaMuxer = MediaMuxer(mPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
                mVideoTrackId = mMediaMuxer?.addTrack(format) ?: -1
                mMediaMuxer?.start()
            }
        }
    }

    fun release() {
        val bufferInfo = MediaCodec.BufferInfo()
        val buffer = ByteBuffer.allocate(0)
        bufferInfo.set(0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
        mMediaMuxer?.writeSampleData(mVideoTrackId, buffer, bufferInfo)
        mMediaCodec?.stop()
        mMediaCodec?.release()
        mMediaMuxer?.stop()
        mMediaMuxer?.release()
        Log.d(TAG, "save video in $mPath")
    }

    fun getSurface(): Surface = mRecordSurface

    fun startCodec(width: Int, height: Int) {
        mMediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
            .also { format ->
                format.setInteger(
                    MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar
                    //MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
                )
                format.setInteger(MediaFormat.KEY_BIT_RATE, 5 * 1024 * 1024)
                format.setInteger(MediaFormat.KEY_FRAME_RATE, 60)
                format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 15)
            }

        val mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        mediaCodec.codecInfo.getCapabilitiesForType(MediaFormat.MIMETYPE_VIDEO_AVC).colorFormats.forEach {
            Log.d(TAG, "support format $it")
        }
        //val mediaCodec = MediaCodec.createByCodecName("OMX.qcom.video.encoder.avc")
        mediaCodec.setCallback(mMediaCodecCb)
        mediaCodec.configure(mMediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mediaCodec.setInputSurface(mRecordSurface)
        mediaCodec.start()
        mMediaCodec = mediaCodec
    }
}