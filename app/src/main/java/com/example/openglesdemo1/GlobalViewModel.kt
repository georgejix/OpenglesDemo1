package com.example.openglesdemo1

import android.content.res.Configuration
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

object GlobalViewModel : ViewModel() {
    private val TAG = javaClass.simpleName
    val mThemeDay = "day"
    val mThemeNight = "night"
    var mTheme = ObservableField<String>("")

    fun setTheme(uiMode: Int) {
        if (uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            // 处于深色模式
            Log.d(TAG, "处于深色模式")
            mTheme.set(mThemeNight)
        } else {
            // 处于浅色模式
            Log.d(TAG, "处于浅色模式")
            mTheme.set(mThemeDay)
        }
    }
}