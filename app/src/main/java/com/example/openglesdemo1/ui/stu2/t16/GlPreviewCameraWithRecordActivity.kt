package com.example.openglesdemo1.ui.stu2.t16

import android.Manifest
import android.os.Bundle
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2

class GlPreviewCameraWithRecordActivity : BaseActivity2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission(
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
            ), 0
        )
    }

    override fun getPermissions(get: Boolean, requestCode: Int) {
        if (get) {
            setContentView(R.layout.activity_gl_preview_camera_with_record)
        }
    }
}