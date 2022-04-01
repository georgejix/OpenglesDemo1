package com.example.openglesdemo1.utils

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

object ToastUtil {
    private var toast: Toast? = null
    private var mNeedCancel = true
    private var mLastThreadId: Long = 0

    fun showToast(msg: String?) {
        showToast(null, msg)
    }

    fun showToast(context: Context?, msg: String?) {
        if (null == toast || mLastThreadId != Thread.currentThread().id) {
            toast = Toast(AppCore.getInstance().context)
            try {
                toast?.setText("")
            } catch (e: Exception) {
                mNeedCancel = false
                toast = Toast.makeText(AppCore.getInstance().context, "", Toast.LENGTH_SHORT)
            }
            mLastThreadId = Thread.currentThread().id
        }
        toast?.setGravity(Gravity.CENTER, 0, 0)
        toast?.view.apply {
            if (this is LinearLayout) {
                val v2 = getChildAt(0)
                if (v2 is TextView) {
                    v2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                }
            }
        }
        if (mNeedCancel) {
            toast?.cancel()
        }
        toast?.setText(msg)
        toast?.show()
    }
}