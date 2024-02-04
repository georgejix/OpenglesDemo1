package com.example.openglesdemo1.ui.mediacodec.t2

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import kotlinx.android.synthetic.main.activity_mediacodec_save_video.layout_preview

class MediaCodecSaveVideoActivity : BaseActivity2() {
    private val TAG = "MediaCodecSaveVideoActivity"
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
                if (layout_preview.height > 0) {
                    openCamera(holder.surface)
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                Log.d(TAG, "surfaceDestroyed")
                closeCamera()
            }

        })
        layout_preview.addView(surfaceView)
    }
}