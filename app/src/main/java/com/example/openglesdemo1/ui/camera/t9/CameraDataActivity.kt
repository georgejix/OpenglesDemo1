package com.example.openglesdemo1.ui.camera.t9

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2

class CameraDataActivity : BaseActivity2() {
    private val TAG = "CameraDataActivity"

    private val mTextureView: TextureView by lazy { findViewById<TextureView>(R.id.textureView) }
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mSurface: Surface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_camera2_data)
        mTextureView.surfaceTextureListener =
            object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(
                    surface: SurfaceTexture, width: Int, height: Int
                ) {
                    mSurfaceTexture = surface
                    mSurface = Surface(surface)
                    initCamera()
                }

                override fun onSurfaceTextureSizeChanged(
                    surface: SurfaceTexture, width: Int, height: Int
                ) {
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                    return true
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                }
            }
    }

    private fun initCamera() {
        val camera = Camera.open(0)
        camera.parameters.supportedPictureSizes.forEach { size ->
            if (720 == size.height) {
                Log.d(TAG, "${size.width}  ${size.height}")
                //previewsize
                mSurfaceTexture?.setDefaultBufferSize(size.width, size.height)
                if (mTextureView.layoutParams is ConstraintLayout.LayoutParams) {
                    val param =
                        mTextureView.layoutParams as ConstraintLayout.LayoutParams
                    param.dimensionRatio = "w,${size.width}:${size.height}"
                    mTextureView.layoutParams = param
                }
            }
        }
        camera.setDisplayOrientation(90)
        camera.setPreviewTexture(mSurfaceTexture)
        camera.setPreviewCallback(object : Camera.PreviewCallback {
            override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
                Log.d(TAG, "camera data get")
            }
        })
        camera.startPreview()
    }
}