package com.example.openglesdemo1.ui.mediacodec.t8

import android.annotation.SuppressLint
import android.media.MediaCodec.BufferInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.getOutputVideoPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

class CutVideoActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName

    private val mJointTv: TextView by lazy { findViewById(R.id.tv_joint) }
    private val mJoint2Tv: TextView by lazy { findViewById(R.id.tv_joint2) }
    private val mTip: TextView by lazy { findViewById(R.id.tv_tip) }
    private val mRv: RecyclerView by lazy { findViewById(R.id.rv) }
    private val mVideoAdapter by lazy { VideoAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cut_video)
        mRv.adapter = mVideoAdapter
        queryVideo()
        mJointTv.setOnClickListener {
            jointVideoInfo(true)
        }
        mJoint2Tv.setOnClickListener {
            jointVideoInfo(false)
        }
    }

    private fun queryVideo() {
        val dir = getExternalFilesDir("video")
        dir?.list()?.map {
            val path = "${dir.absolutePath}/$it"
            Log.d(TAG, path)
            VideoBean(path, it, false)
        }?.let {
            mVideoAdapter.setData(it)
            mVideoAdapter.notifyDataSetChanged()
        }
    }

    private fun jointVideoInfo(all: Boolean) {
        val sb = StringBuffer("")
        val list = ArrayList(mVideoAdapter.getData().filter { it.mChecked })
        list.sortWith { v1, v2 ->
            if (v1.mIndex > v2.mIndex) 1 else -1
        }
        list.forEach { sb.append(it.mName).append(",") }
        mTip.text = sb
        CoroutineScope(Dispatchers.IO).launch {
            if (all) {
                jointVideo(list)
            } else {
                jointVideo2(list)
            }
        }
    }

    @SuppressLint("WrongConstant")
    private fun jointVideo(list: ArrayList<VideoBean>) {
        // 伪代码和核心逻辑
        val muxer = MediaMuxer(
            getOutputVideoPath("jointVideo.mp4"),
            MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
        )
        val extractors = list.map { bean -> MediaExtractor().also { it.setDataSource(bean.mPath) } }
        var fromVideoTrackIndex = -1
        var toVideoTrackIndex = -1
        var totalDuration = 0L
        extractors.forEach { extractor ->
            repeat(extractor.trackCount) { trackIndex ->
                if (true == extractor.getTrackFormat(trackIndex).getString(MediaFormat.KEY_MIME)
                        ?.contains("video/")
                ) {
                    fromVideoTrackIndex = trackIndex
                    extractor.selectTrack(trackIndex)
                    if (toVideoTrackIndex < 0) {
                        toVideoTrackIndex = muxer.addTrack(extractor.getTrackFormat(trackIndex))
                        muxer.start()
                    }
                }
            }
            // 调整 extractor 的时间偏移量
            extractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            val bufferInfo = BufferInfo()
            val data = ByteBuffer.allocate(1920 * 1080)
            while (true) {
                var readSize = extractor.readSampleData(data, 0)
                if (readSize < 0) break
                Log.d(TAG, "sampleTime=${extractor.sampleTime}")
                bufferInfo.presentationTimeUs = extractor.sampleTime + totalDuration
                bufferInfo.flags = extractor.sampleFlags
                bufferInfo.size = readSize
                muxer.writeSampleData(toVideoTrackIndex, data, bufferInfo)
                extractor.advance()
            }
            totalDuration += extractor.getTrackFormat(fromVideoTrackIndex)
                .getLong(MediaFormat.KEY_DURATION)
            extractor.release()
        }
        muxer.stop()
        muxer.release()
        CoroutineScope(Dispatchers.Main).launch { mTip.text = "finish" }
    }

    /**
     * 第一个视频取一半，最后一个视频一半
     */
    @SuppressLint("WrongConstant")
    private fun jointVideo2(list: ArrayList<VideoBean>) {
        // 伪代码和核心逻辑
        val muxer = MediaMuxer(
            getOutputVideoPath("jointVideo2.mp4"),
            MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
        )
        val extractors = list.map { bean -> MediaExtractor().also { it.setDataSource(bean.mPath) } }
        var fromVideoTrackIndex = -1
        var toVideoTrackIndex = -1
        var totalDuration = 0L
        extractors.forEachIndexed { index, extractor ->
            repeat(extractor.trackCount) { trackIndex ->
                if (true == extractor.getTrackFormat(trackIndex).getString(MediaFormat.KEY_MIME)
                        ?.contains("video/")
                ) {
                    fromVideoTrackIndex = trackIndex
                    extractor.selectTrack(trackIndex)
                    if (toVideoTrackIndex < 0) {
                        toVideoTrackIndex = muxer.addTrack(extractor.getTrackFormat(trackIndex))
                        muxer.start()
                    }
                }
            }
            val duration =
                extractor.getTrackFormat(fromVideoTrackIndex).getLong(MediaFormat.KEY_DURATION)
            val bufferInfo = BufferInfo()
            val data = ByteBuffer.allocate(1920 * 1080)

            // 调整 extractor 的时间偏移量
            if (0 == index) {
                extractor.seekTo(duration / 2, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            } else {
                extractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            }

            while (true) {
                var readSize = extractor.readSampleData(data, 0)
                if (readSize < 0 || (extractors.size - 1 == index && extractor.sampleTime >= duration / 2)) break
                Log.d(TAG, "sampleTime=${extractor.sampleTime}")
                bufferInfo.presentationTimeUs = extractor.sampleTime + totalDuration
                bufferInfo.flags = extractor.sampleFlags
                bufferInfo.size = readSize
                muxer.writeSampleData(toVideoTrackIndex, data, bufferInfo)
                extractor.advance()
            }
            totalDuration += duration
            extractor.release()
        }
        muxer.stop()
        muxer.release()
        CoroutineScope(Dispatchers.Main).launch { mTip.text = "finish" }
    }
}