package com.example.openglesdemo1.ui.base

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

open class BaseActivity2 : Activity() {
    var mContext: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
    }

    fun requestPermission(permissions: List<String>, requestCode: Int) {
        var needRequestPermissions = ArrayList<String>()
        permissions.forEach {
            if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(this, it)
            ) {
                needRequestPermissions.add(it)
            }
        }
        if (0 == needRequestPermissions.size) {
            getPermissions(true, requestCode)
        } else {
            ActivityCompat.requestPermissions(
                this, needRequestPermissions.toArray(arrayOf()), requestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        getPermissions(permissions.all {
            PackageManager.PERMISSION_GRANTED ==
                    ContextCompat.checkSelfPermission(this, it)
        }, requestCode)
    }

    open fun getPermissions(get: Boolean, requestCode: Int) {

    }

    //隐藏状态栏
    fun changeStatusBars(isShow: Boolean, view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (isShow) {
                window?.insetsController?.show(WindowInsets.Type.statusBars())
            } else {
                window?.insetsController?.hide(WindowInsets.Type.statusBars())
            }
        } else {
            ViewCompat.getWindowInsetsController(view).let { controller ->
                if (isShow) {
                    controller?.show(WindowInsetsCompat.Type.statusBars())
                } else {
                    controller?.hide(WindowInsetsCompat.Type.statusBars())
                }
            }
        }
    }
}