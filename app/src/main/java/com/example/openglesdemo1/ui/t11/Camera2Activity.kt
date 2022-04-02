package com.example.openglesdemo1.ui.t11

import android.Manifest
import android.os.Bundle
import android.view.View
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import com.example.openglesdemo1.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_camera2.*

class Camera2Activity : BaseActivity2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)
        changeStatusBars(false, btn_take_photo)
        requestPermissions(arrayOf(Manifest.permission.CAMERA), 0)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_take_photo -> {
                ToastUtil.showToast("take photo")
            }
        }
    }

    override fun getPermissions(get: Boolean, requestCode: Int) {
        super.getPermissions(get, requestCode)
        if (get) {
            ToastUtil.showToast("get")
        }
    }

}