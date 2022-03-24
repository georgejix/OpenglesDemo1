package com.example.openglesdemo1.ui.base

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

open class BaseActivity2 : Activity() {
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
}