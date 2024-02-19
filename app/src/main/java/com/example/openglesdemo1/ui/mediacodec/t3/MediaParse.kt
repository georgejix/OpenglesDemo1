package com.example.openglesdemo1.ui.mediacodec.t3

import android.media.MediaExtractor
import android.media.MediaFormat
import java.nio.ByteBuffer

class MediaParse(val mPath: String) {
    private var mMediaExtractor: MediaExtractor? = null
    private var mCurSampleTime: Long = 0
    private var mStartTime: Long = 0

    init {
        mMediaExtractor = MediaExtractor()
        mMediaExtractor?.setDataSource(mPath)
        mStartTime = System.currentTimeMillis()
    }

    fun getVideoTrackIndex(): Int? {
        var trackIndex: Int? = null
        mMediaExtractor?.let {
            for (index in 0 until it.trackCount) {
                if (true == it.getTrackFormat(index).getString(MediaFormat.KEY_MIME)
                        ?.contains("video/")
                ) {
                    trackIndex = index
                }
            }
        }
        return trackIndex
    }

    fun getVideoFormat(): MediaFormat? {
        var format: MediaFormat? = null
        mMediaExtractor?.let {
            for (index in 0 until it.trackCount) {
                if (true == it.getTrackFormat(index).getString(MediaFormat.KEY_MIME)
                        ?.contains("video/")
                ) {
                    format = mMediaExtractor?.getTrackFormat(index)
                }
            }
        }
        return format
    }

    fun readBuffer(trackIndex: Int, byteBuffer: ByteBuffer): Int {
        mMediaExtractor?.selectTrack(trackIndex)
        byteBuffer.clear()
        val result = mMediaExtractor?.readSampleData(byteBuffer, 0)
        mMediaExtractor?.advance()
        mCurSampleTime = mMediaExtractor?.sampleTime ?: 0L
        return result ?: -1
    }

    fun getCurrentTimestamp(): Long {
        return mCurSampleTime
    }

    fun getDelayTime(): Long {
        val t1 = System.currentTimeMillis() - mStartTime
        val t2 = getCurrentTimestamp() / 1000
        val t = t2 - t1
        return if (t > 0) t else 0
    }

    fun stop() {
        mMediaExtractor?.release()
        mMediaExtractor = null
    }

}