package com.example.openglesdemo1.ui.mediacodec.t2

import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import com.example.openglesdemo1.utils.getVideoPath
import kotlinx.android.synthetic.main.activity_mediacodec_save_video.btn_record
import kotlinx.android.synthetic.main.activity_mediacodec_save_video.layout_preview

class MediaCodecSaveVideoActivity : BaseActivity2() {
    private val TAG = "MediaCodecSaveVideoActivity"
    private var mInit = false
    private var mWidth = 0
    private var mHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mediacodec_save_video)

        val surfaceView = SurfaceView(this)
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                Log.d(TAG, "surfaceCreated")
                getPreviewSize()?.let {
                    val param = layout_preview.layoutParams
                    if (param is ConstraintLayout.LayoutParams) {
                        param.dimensionRatio = "w,${it.width}:${it.height}"
                    }
                    layout_preview.layoutParams = param
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder, format: Int,
                width: Int, height: Int
            ) {
                Log.d(TAG, "surfaceChanged $width  $height")
                holder.setFixedSize(width, height)
                mWidth = width
                mHeight = height
                if (layout_preview.height > 0) {
                    openCamera(listOf(holder.surface))
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                Log.d(TAG, "surfaceDestroyed")
                closeCamera()
            }

        })
        layout_preview.addView(surfaceView)
    }

    override fun onResume() {
        super.onResume()
        if (!mInit) {
            initListener()
            mInit = true
        }
    }

    private fun initListener() {
        btn_record.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                startRecord()
            } else {
                stopRecord()
            }
        }
    }

    private fun startRecord() {
        val path = getVideoPath("${System.currentTimeMillis()}.mp4")
        val muxer = LocalVideoMuxer(path)
        val videoFormat = LocalVideoEncoder(mWidth, mHeight, muxer)
    }

    private fun stopRecord() {

    }
}