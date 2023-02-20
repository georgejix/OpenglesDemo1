package com.example.openglesdemo1.ui.stu2.t2

import android.Manifest
import android.opengl.GLSurfaceView
import android.os.Bundle
import com.example.openglesdemo1.ui.base.BaseActivity2
import com.example.openglesdemo1.utils.ToastUtil
import java.util.concurrent.atomic.AtomicBoolean

/**
 * camera1 + opengl
 */
class GlPreviewCameraActivity : BaseActivity2() {
    private val TAG = "RecordByCameraActivity"
    private val mGlSurface: GLSurfaceView by lazy { GLSurfaceView(this) }
    private var mGlPreviewCameraRender: GlPreviewCameraRender? = null
    private var mIsPreview = AtomicBoolean(false)
    private var mIsRenderCreated = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            mGlPreviewCameraRender ?: let {
                mGlSurface.setEGLContextClientVersion(3)
                mGlPreviewCameraRender =
                    GlPreviewCameraRender(mGlSurface, object : GlPreviewCameraRender.Listener {
                        override fun onSurfaceCreated() {
                            mIsRenderCreated.set(true)
                            startPreview()
                        }
                    })
                mGlSurface.setRenderer(mGlPreviewCameraRender)
                setContentView(mGlSurface)
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
        mGlPreviewCameraRender?.release()
        println("$TAG release preview")
    }

    private fun startPreview() {
        if (!mIsRenderCreated.get() || mIsPreview.get() || isFinishing || isDestroyed) {
            return
        }
        println("$TAG start preview")
        mGlPreviewCameraRender?.start()
        mIsPreview.set(true)
    }

    private fun stopPreview() {
        if (!mIsRenderCreated.get() || !mIsPreview.get() || isFinishing || isDestroyed) {
            return
        }
        println("$TAG stop preview")
        mGlPreviewCameraRender?.stop()
        mIsPreview.set(false)
    }
}