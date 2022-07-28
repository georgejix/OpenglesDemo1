package com.example.openglesdemo1.utils

import android.app.Application
import android.content.Context
import android.os.Environment
import java.io.File

object AppCore {
    var application: Application? = null
    var path: String = ""

    fun init(application: Application) {
        this.application = application
        path = Environment.getExternalStorageDirectory()
            .absolutePath + File.separator + "androidDemo"
        val projectFile = File(path)
        if (!projectFile.exists()) {
            projectFile.mkdirs()
        }
    }

    fun getContext(): Context = application!!.applicationContext
}
