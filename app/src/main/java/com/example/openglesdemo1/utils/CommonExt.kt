package com.example.openglesdemo1.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import java.io.File
import kotlin.coroutines.Continuation

fun Context.getVideoPath(name: String): String {
    val file = File(getExternalFilesDir("video")!!.absolutePath, name)
    file.delete()
    file.createNewFile()
    Log.d("path", file.absolutePath)
    return file.absolutePath
}

suspend inline fun <T> suspendCoroutineWithTimeout(
    timeout: Long, crossinline block: (Continuation<T>) -> Unit
) = withTimeout(timeout) {
    suspendCancellableCoroutine(block = block)
}