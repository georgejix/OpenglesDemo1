package com.example.openglesdemo1.utils

import android.content.Context
import android.util.Log
import java.io.File

fun Context.getVideoPath(name: String): String {
    val path = File("${cacheDir.absolutePath}${File.separator}video", name).absolutePath
    Log.d("path", path)
    return path
}