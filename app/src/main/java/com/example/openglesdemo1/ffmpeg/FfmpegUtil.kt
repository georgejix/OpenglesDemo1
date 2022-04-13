package com.example.openglesdemo1.ffmpeg

class FfmpegUtil {
    init {
        System.loadLibrary("ffmpegTest")
        System.loadLibrary("avcodec")
        System.loadLibrary("avfilter")
        System.loadLibrary("avformat")
        System.loadLibrary("avutil")
        System.loadLibrary("postproc")
        System.loadLibrary("swresample")
        System.loadLibrary("swscale")
    }

    external fun initVideo(outputPath: String?): Int
    external fun writeVideo(): Int
    external fun stopVideo(): Int
}