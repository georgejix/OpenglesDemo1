package com.example.openglesdemo1

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.example.openglesdemo1.utils.AppCore

class BaseApplication : Application() {
    private val TAG = javaClass.simpleName

    companion object {
        var mContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
        AppCore.init(this)
        GlobalViewModel.setTheme(resources.configuration.uiMode)
    }

    override fun onTerminate() {
        super.onTerminate()
        mContext = null
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged")
        GlobalViewModel.setTheme(newConfig.uiMode)
    }
}