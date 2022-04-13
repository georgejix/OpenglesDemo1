package com.example.openglesdemo1.ui.t13

import android.Manifest
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import com.example.openglesdemo1.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_record_by_camera2.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * camera2 + opengl
 */
class RecordByCamera2Activity : BaseActivity2() {
    private val TAG = "RecordByCamera2Activity"
    private val mGlSurface: GLSurfaceView by lazy { GLSurfaceView(this) }
    private var mRecordByCameraRender: RecordByCamera2Render? = null
    private var mIsPreview = AtomicBoolean(false)
    private var mIsRenderCreated = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_by_camera2)
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
            mRecordByCameraRender ?: let {
                mGlSurface.setEGLContextClientVersion(3)
                mRecordByCameraRender =
                    RecordByCamera2Render(mGlSurface, object : RecordByCamera2Render.Listener {
                        override fun onSurfaceCreated() {
                            mIsRenderCreated.set(true)
                            startPreview()
                        }
                    })
                mRecordByCameraRender?.mSize?.apply {
                    val param = layout_preview.layoutParams as ConstraintLayout.LayoutParams
                    param.dimensionRatio = "w,${width}:${height}"
                }
                mGlSurface.setRenderer(mRecordByCameraRender)
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
        mRecordByCameraRender?.release()
        println("$TAG release preview")
    }

    private fun startPreview() {
        if (!mIsRenderCreated.get() || mIsPreview.get() || isFinishing || isDestroyed) {
            return
        }
        println("$TAG start preview")
        mRecordByCameraRender?.start()
        mIsPreview.set(true)
    }

    private fun stopPreview() {
        if (!mIsRenderCreated.get() || !mIsPreview.get() || isFinishing || isDestroyed) {
            return
        }
        println("$TAG stop preview")
        mRecordByCameraRender?.stop()
        mIsPreview.set(false)
    }
}