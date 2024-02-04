package com.example.openglesdemo1.utils

import android.app.Application
import android.content.Context

object AppCore {
    var application: Application? = null

    fun init(application: Application) {
        this.application = application
    }


    fun getContext(): Context = application!!.applicationContext
}
