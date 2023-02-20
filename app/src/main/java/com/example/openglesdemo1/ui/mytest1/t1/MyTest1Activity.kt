package com.example.openglesdemo1.ui.mytest1.t1

import android.Manifest
import android.os.Bundle
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2
import kotlinx.android.synthetic.main.activity_mytest1.*

class MyTest1Activity : BaseActivity2() {
    private var mInitView = true
    private var mMyTest01Render: MyTest01Render? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mytest1)
        mInitView = true
    }

    override fun onResume() {
        super.onResume()
        if (mInitView) {
            requestPermission(
                listOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 0
            )
            initView()
            mInitView = false
        }
    }

    override fun getPermissions(get: Boolean, requestCode: Int) {
        if (0 == requestCode && get) {
            initView()
        }
    }

    private fun initView() {
        gl_surface.setEGLContextClientVersion(3)
        mMyTest01Render = MyTest01Render(mContext, object : MyTest01Render.Listener {
            override fun onOpenCamera() {
            }

        })
        gl_surface.setRenderer(mMyTest01Render)
    }
}