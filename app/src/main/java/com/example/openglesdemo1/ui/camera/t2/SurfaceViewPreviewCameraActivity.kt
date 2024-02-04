package com.example.openglesdemo1.ui.camera.t2

import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import kotlinx.android.synthetic.main.activity_camera2_surfaceview.layout_preview

class SurfaceViewPreviewCameraActivity : BaseActivity2() {

    private val TAG = "SurfaceViewPreviewCamera"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2_surfaceview)
        val surfaceView = SurfaceView(this)
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                getPreviewSize()?.let {
                    val param = layout_preview.layoutParams
                    if (param is ConstraintLayout.LayoutParams) {
                        param.dimensionRatio = "w,${it.width}:${it.height}"
                    }
                    layout_preview.layoutParams = param
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder, format: Int, width: Int, height: Int
            ) {
                /**
                 * 取相机支持的最大尺寸比例，设置holder和view尺寸
                 */
                holder.setFixedSize(width, height)
                if (layout_preview.height > 0) {
                    openCamera(listOf(holder.surface))
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                closeCamera()
            }
        })
        layout_preview.addView(surfaceView)
    }

}