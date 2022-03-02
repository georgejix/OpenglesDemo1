package com.example.openglesdemo1

import android.app.Application
import com.example.openglesdemo1.utils.AppCore

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCore.getInstance().init(this)
    }
}