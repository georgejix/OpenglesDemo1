package com.example.openglesdemo1.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import java.io.File
import kotlin.coroutines.Continuation

/**
 * Environment.getExternalStoragePublicDirectory(type)
 * Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
 */
fun Context.getOutputVideoPath(name: String = "testVideo.mp4"): String {
    val file = File(getExternalFilesDir("video")!!.absolutePath, name)
    file.delete()
    file.createNewFile()
    Log.d("path", "getOutputVideoPath ${file.absolutePath}")
    return file.absolutePath
}

fun Context.getInputVideoPath(): String {
    val file = File(getExternalFilesDir("video")!!.absolutePath, "testVideo.mp4")
    Log.d("path", "getInputVideoPath ${file.absolutePath}")
    return file.absolutePath
}

suspend inline fun <T> suspendCoroutineWithTimeout(
    timeout: Long, crossinline block: (Continuation<T>) -> Unit
) = withTimeout(timeout) {
    suspendCancellableCoroutine(block = block)
}