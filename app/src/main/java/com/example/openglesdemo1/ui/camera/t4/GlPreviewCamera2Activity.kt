package com.example.openglesdemo1.ui.camera.t4

import android.Manifest
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import com.example.openglesdemo1.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_gl_preview_camera2.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * camera2 + opengl
 */
class GlPreviewCamera2Activity : BaseActivity2() {
    private val TAG = "RecordByCamera2Activity"
    private val mGlSurface: GLSurfaceView by lazy { GLSurfaceView(this) }
    private var mGlPreviewCameraActivityRender: GlPreviewCamera2Render? = null
    private var mIsPreview = AtomicBoolean(false)
    private var mIsRenderCreated = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gl_preview_camera2)
        requestPermission(
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 0
        )
    }

    override fun getPermissions(get: Boolean, requestCode: Int) {
        super.getPermissions(get, requestCode)
        if (0 == requestCode && get) {
            println("$TAG get permission")
            mGlPreviewCameraActivityRender ?: let {
                mGlSurface.setEGLContextClientVersion(3)
                mGlPreviewCameraActivityRender =
                    GlPreviewCamera2Render(mGlSurface, object : GlPreviewCamera2Render.Listener {
                        override fun onSurfaceCreated() {
                            mIsRenderCreated.set(true)
                            startPreview()
                        }
                    })
                mGlPreviewCameraActivityRender?.mSize?.apply {
                    val param = layout_preview.layoutParams as ConstraintLayout.LayoutParams
                    param.dimensionRatio = "w,${width}:${height}"
                }
                mGlSurface.setRenderer(mGlPreviewCameraActivityRender)
                layout_preview.removeAllViews()
                layout_preview.addView(mGlSurface)
            }
        } else {
            ToastUtil.showToast("no permission")
        }
    }

    override fun onResume() {
        super.onResume()
        startPreview()
    }

    override fun onPause() {
        super.onPause()
        stopPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        mGlPreviewCameraActivityRender?.release()
        println("$TAG release preview")
    }

    fun onClick(view: View) {
        when (view.id) {
        }
    }

    private fun startPreview() {
        if (!mIsRenderCreated.get() || mIsPreview.get() || isFinishing || isDestroyed) {
            return
        }
        println("$TAG start preview")
        mGlPreviewCameraActivityRender?.start()
        mIsPreview.set(true)
    }

    private fun stopPreview() {
        if (!mIsRenderCreated.get() || !mIsPreview.get() || isFinishing || isDestroyed) {
            return
        }
        println("$TAG stop preview")
        mGlPreviewCameraActivityRender?.stop()
        mIsPreview.set(false)
    }
}