package com.example.openglesdemo1.utils

import android.content.Context
import android.util.Log
import java.io.File

fun Context.getVideoPath(name: String): String {
    val file = File(getExternalFilesDir("video")!!.absolutePath, name)
    file.delete()
    file.createNewFile()
    Log.d("path", file.absolutePath)
    return file.absolutePath
}