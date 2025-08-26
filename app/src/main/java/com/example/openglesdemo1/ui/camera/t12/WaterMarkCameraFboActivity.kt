package com.example.openglesdemo1.ui.camera.t12

import android.Manifest
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import kotlinx.android.synthetic.main.activity_camera2_surfaceview.layout_preview

class WaterMarkCameraFboActivity : BaseActivity2() {
    private val mCombineRender by lazy { CombineRender("combine") }
    private val mPreviewRender by lazy { PreviewRender("preview") }

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
                    mCombineRender.initEgl() { c ->
                        mPreviewRender.initEgl(holder.surface, c)
                        mPreviewRender.changeView(width, height)
                    }
                    mCombineRender.changeView(width, height)
                    mCombineRender.createSv { st ->
                        val surface2 = Surface(st.also { s ->
                            s.setDefaultBufferSize(1080, 1920)
                            s.setOnFrameAvailableListener {
                                mCombineRender.draw() { sharedId, fence ->
                                    mPreviewRender.draw(sharedId, fence)
                                }
                            }
                        })
                        openCamera(listOf(surface2))
                    }
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                closeCamera()
            }
        })
        layout_preview.addView(surfaceView)
    }

    override fun onResume() {
        super.onResume()
        requestPermission(listOf(Manifest.permission.CAMERA), 0)
    }
}