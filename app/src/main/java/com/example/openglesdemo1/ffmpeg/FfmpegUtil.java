package com.example.openglesdemo1.ffmpeg;

public class FfmpegUtil {
    static {
        System.loadLibrary("ffmpegTest");

        System.loadLibrary("avcodec");
        System.loadLibrary("avfilter");
        System.loadLibrary("avformat");
        System.loadLibrary("avutil");
        System.loadLibrary("postproc");
        System.loadLibrary("swresample");
        System.loadLibrary("swscale");
    }

    public native int initVideo(String outputPath);
    public native int stopVideo();
}
