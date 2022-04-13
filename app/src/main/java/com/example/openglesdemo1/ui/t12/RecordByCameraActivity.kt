package com.example.openglesdemo1.ui.t12

import android.Manifest
import android.opengl.GLSurfaceView
import android.os.Bundle
import com.example.openglesdemo1.ui.base.BaseActivity2
import com.example.openglesdemo1.utils.ToastUtil
import java.util.concurrent.atomic.AtomicBoolean

/**
 * camera1 + opengl
 */
class RecordByCameraActivity : BaseActivity2() {
    private val TAG = "RecordByCameraActivity"
    private val mGlSurface: GLSurfaceView by lazy { GLSurfaceView(this) }
    private var mRecordByCameraRender: RecordByCameraRender? = null
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
            mRecordByCameraRender ?: let {
                mGlSurface.setEGLContextClientVersion(3)
                mRecordByCameraRender =
                    RecordByCameraRender(mGlSurface, object : RecordByCameraRender.Listener {
                        override fun onSurfaceCreated() {
                            mIsRenderCreated.set(true)
                            startPreview()
                        }
                    })
                mGlSurface.setRenderer(mRecordByCameraRender)
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