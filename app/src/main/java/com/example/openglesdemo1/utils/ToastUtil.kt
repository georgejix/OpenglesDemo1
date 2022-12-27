package com.example.openglesdemo1.utils

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

object ToastUtil {
    private var toast: Toast? = null

    fun showToast(msg: String?) {
        showToast(null, msg)
    }

    fun showToast(context: Context?, msg: String?) {
        context ?: return
        msg ?: return
        toast?.cancel()
        toast = Toast.makeText(AppCore.getContext(), "", Toast.LENGTH_SHORT)
        toast?.setGravity(Gravity.CENTER, 0, 0)
        toast?.view.apply {
            if (this is LinearLayout) {
                val v2 = getChildAt(0)
                if (v2 is TextView) {
                    v2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                }
            }
        }
        toast?.setText(msg)
        toast?.show()
    }
}