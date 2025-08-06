package com.example.openglesdemo1.ui.mediacodec.t6

import android.Manifest
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import kotlinx.android.synthetic.main.activity_back_record.frame3
import kotlinx.android.synthetic.main.activity_back_record.tv_refresh
import kotlinx.android.synthetic.main.activity_back_record.tv_toggle
import java.nio.ByteBuffer
import java.text.SimpleDateFormat

class BackRecordActivity : BaseActivity2() {
    private val TAG = javaClass.simpleName
    private val mRecordMap: HashMap<String, BackRecordBean> = HashMap()
    private val mSdf by lazy { SimpleDateFormat("yyyy-MM-dd-HH-mm-ss") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back_record)
        tv_toggle.setOnClickListener {
            toggleCamera("1")
        }
        tv_refresh.setOnClickListener {
            refreshCamera("1")
        }
        requestPermissions(arrayOf(Manifest.permission.CAMERA), 0)
    }

    private fun toggleCamera(id:String) {
        if (isPreviewed(frame3)) {
            tv_toggle.text = "start"
            mSessionMap[id]?.close()
            mCameraMap[id]?.close()
            mRecordMap[id]?.let { bean ->
                val bufferInfo = MediaCodec.BufferInfo()
                val buffer = ByteBuffer.allocate(0)
                bufferInfo.set(0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                bean.mVideoTrack?.let { trackId ->
                    bean.mMediaMuxer?.writeSampleData(trackId, buffer, bufferInfo)
                }
                bean.mWriteEnd = true
                Log.d(TAG, "write end")
                bean.mMediaMuxer?.stop()
                bean.mMediaMuxer?.release()
                bean.mMediaCodec?.stop()
                bean.mMediaCodec?.release()
            }
            removePreview(id, frame3)
        } else {
            tv_toggle.text = "end"
            mRecordMap[id] = BackRecordBean()
            addPreview(id, frame3)
        }
    }

    private fun refreshCamera(id: String) {
        mRecordMap[id]?.let {
            mSessionMap[id]?.close()
            mCameraMap[id]?.close()
            frame3.removeAllViews()
            addPreview(id, frame3)
        }
    }

    private fun isPreviewed(layout: ViewGroup): Boolean = layout.childCount > 0

    private fun removePreview(id: String, layout: ViewGroup) {
        layout.removeAllViews()
        mRecordMap.remove(id)
    }

    private fun addPreview(id: String, layout: ViewGroup) {
        val surfaceView = SurfaceView(this)
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {
            }

            override fun surfaceChanged(
                surfaceHolder: SurfaceHolder, format: Int, width: Int, height: Int
            ) {
                mRecordMap[id]?.mMediaMuxer?.let {
                    mRecordMap[id]?.mSurface?.let {
                        openCamera(listOf(surfaceHolder.surface, it), id)
                    }
                } ?: let {
                    localSurfaceChanged(id, surfaceHolder, format, width, height)
                }
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
                /*mRecordMap.forEach { (k, v) ->
                    v.mVideoTrack?.let { trackId ->
                        val bufferInfo = MediaCodec.BufferInfo()
                        val buffer = ByteBuffer.allocate(0)
                        bufferInfo.set(0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        mRecordMap[k]?.mMediaMuxer?.writeSampleData(trackId, buffer, bufferInfo)
                        mRecordMap[k]?.mWriteEnd = true
                        Log.d(TAG, "write end")
                    }
                    v.mMediaMuxer?.stop()
                    v.mMediaMuxer?.release()
                    v.mMediaCodec?.stop()
                    v.mMediaCodec?.release()
                }*/
                /*mSessionMap.forEach { (_, session) -> session?.stopRepeating() }
                mCameraMap.forEach { (_, cameraDevice) -> cameraDevice?.close() }*/
            }

        })
        layout.addView(surfaceView)
    }

    private fun localSurfaceChanged(
        id: String, surfaceHolder: SurfaceHolder, format: Int, width: Int, height: Int
    ) {
        Log.d(TAG, "localSurfaceChanged format ${format}")
        getMediaCodec(id, width, height, object : MediaCodec.Callback() {
            override fun onInputBufferAvailable(p0: MediaCodec, p1: Int) {
                Log.d(TAG, "onInputBufferAvailable")
            }

            override fun onOutputBufferAvailable(
                mediaCodec: MediaCodec, index: Int, info: MediaCodec.BufferInfo
            ) {
                //Log.d(TAG, "onOutputBufferAvailable ${info.offset} ${info.size}")
                if (true != mRecordMap[id]?.mWriteEnd) {
                    if (MediaCodec.BUFFER_FLAG_KEY_FRAME == info.flags) {
                        Log.d(TAG, "onOutputBufferAvailable key frame")
                    }
                    mRecordMap[id]?.mVideoTrack?.let { trackId ->
                        runCatching {
                            mediaCodec.getOutputBuffer(index)?.let {
                                mRecordMap[id]?.mMediaMuxer?.writeSampleData(trackId, it, info)
                            }
                            mediaCodec.releaseOutputBuffer(index, false)
                        }
                    }
                }
            }

            override fun onError(p0: MediaCodec, p1: MediaCodec.CodecException) {
            }

            override fun onOutputFormatChanged(p0: MediaCodec, p1: MediaFormat) {
                Log.d(TAG, "onOutputFormatChanged")
                val time = mSdf.format(System.currentTimeMillis())
                val path = "${getExternalFilesDir("video")?.absolutePath}/camera$id$time.mp4"
                val mediaMuxer = MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
                mRecordMap[id]?.mMediaMuxer = mediaMuxer
                mRecordMap[id]?.mMediaCodec?.outputFormat?.let {
                    Log.d(TAG, "add track $it")
                    mRecordMap[id]?.mVideoTrack = mRecordMap[id]?.mMediaMuxer?.addTrack(it)
                }
                mRecordMap[id]?.mMediaMuxer?.start()
            }

        })
        mRecordMap[id]?.mSurface?.let { openCamera(listOf(surfaceHolder.surface, it), id) }
    }

    private fun getMediaCodec(id: String, width: Int, height: Int, cb: MediaCodec.Callback) {
        val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 1280, 720)
        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
        )
        format.setInteger(MediaFormat.KEY_BIT_RATE, 2000 * 1000)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 15)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5)
        mRecordMap[id]?.mMediaFormat = format
        val mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        mediaCodec.setCallback(cb)
        mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        val surface = MediaCodec.createPersistentInputSurface()
        mediaCodec.setInputSurface(surface)
        mRecordMap[id]?.mSurface = surface
        //mRecordMap[id]?.mSurface = mediaCodec.createInputSurface()
        mediaCodec.start()
        mRecordMap[id]?.mMediaCodec = mediaCodec
    }
}