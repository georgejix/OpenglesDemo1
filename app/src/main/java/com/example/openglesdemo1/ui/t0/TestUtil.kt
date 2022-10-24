package com.example.openglesdemo1.ui.t0

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder

class TestUtil {
    companion object {
        fun readTextFileFromResource(context: Context, resourceId: Int): String {
            val sb = StringBuilder("")
            val inputStream = context.resources.openRawResource(resourceId)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferReader = BufferedReader(inputStreamReader)
            var str: String? = null
            while (bufferReader.readLine().also { str = it } != null) {
                sb.append(str).append("\n")
            }
            return sb.toString()
        }
    }
}