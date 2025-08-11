package com.example.openglesdemo1

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import com.example.openglesdemo1.utils.AppCore

class BaseApplication : Application() {
    private val TAG = javaClass.simpleName

    override fun onCreate() {
        super.onCreate()
        AppCore.init(this)
        GlobalViewModel.setTheme(resources.configuration.uiMode)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged")
        GlobalViewModel.setTheme(newConfig.uiMode)
    }
}