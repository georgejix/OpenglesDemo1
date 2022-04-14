package com.example.openglesdemo1.ffmpeg

import android.util.Log

class FfmpegSaveVideoUtil {
    companion object {
        init {
            Log.d("test", "FfmpegSaveVideoUtil")
            System.loadLibrary("ffmpegSaveVideo")
            System.loadLibrary("avcodec")
            System.loadLibrary("avfilter")
            System.loadLibrary("avformat")
            System.loadLibrary("avutil")
            System.loadLibrary("postproc")
            System.loadLibrary("swresample")
            System.loadLibrary("swscale")
        }
    }

    external fun initVideo(outputPath: String?): Int
    external fun writeVideo(): Int
    external fun stopVideo(): Int
}