package com.example.openglesdemo1.ui.camera.t13

import android.Manifest
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import kotlinx.android.synthetic.main.activity_camera2_surfaceview.layout_preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WaterMarkCameraFboYuvActivity : BaseActivity2() {
    private val mCombineRender by lazy { CombineRender("combine") }
    private val mPreviewRender by lazy { PreviewRender("preview") }
    private val mCodecRender by lazy { CodecRender("codec") }
    private val mWaterMarkUtil by lazy { WaterMarkUtil() }
    private var mWaterMarkJob: Job? = null
    private var mNeedWaterMark = true
    private val mCodecUtil by lazy { CodecUtil(this) }

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
                        genWaterMarkJob()
                        mPreviewRender.initEgl(holder.surface, c)
                        mPreviewRender.changeView(width, height)
                        mCodecUtil.startCodec(1080, 1920)
                        mCodecRender.initEgl(mCodecUtil.getSurface(), c)
                        mCodecRender.changeView(1080, 1920)
                    }
                    mCombineRender.changeView(width, height)
                    mCombineRender.createSv { st ->
                        val surface2 = Surface(st.also { s ->
                            s.setDefaultBufferSize(1080, 1920)
                            s.setOnFrameAvailableListener {
                                mCombineRender.draw() { sharedId, fence ->
                                    mPreviewRender.draw(sharedId, fence)
                                    mCodecRender.draw(sharedId, fence)
                                }
                            }
                        })
                        openCamera(listOf(surface2))
                    }
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                closeCamera()
                cancelWaterMarkJob()
                mCodecUtil.release()
            }
        })
        layout_preview.addView(surfaceView)
    }

    override fun onResume() {
        super.onResume()
        requestPermission(listOf(Manifest.permission.CAMERA), 0)
    }

    private fun genWaterMarkJob() {
        mWaterMarkJob?.let { return }
        mWaterMarkJob = CoroutineScope(Dispatchers.IO).launch {
            while (mNeedWaterMark) {
                mCombineRender.updateTopImg(mWaterMarkUtil.genCarInfoBitmap())
                delay(1000)
            }
        }
    }

    private fun cancelWaterMarkJob() {
        mWaterMarkJob?.cancel()
        mWaterMarkJob = null
    }
}