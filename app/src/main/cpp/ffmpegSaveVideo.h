#ifndef FFMPEGTEST.H_SRC
#define FFMPEGTEST.H_SRC

#include <jni.h>

JNIEXPORT jint JNICALL
Java_com_example_openglesdemo1_ffmpeg_FfmpegSaveVideoUtil_initVideo
        (JNIEnv *, jclass, jstring);
JNIEXPORT jint JNICALL
Java_com_example_openglesdemo1_ffmpeg_FfmpegSaveVideoUtil_writeVideo
        (JNIEnv *, jclass);
JNIEXPORT jint JNICALL
Java_com_example_openglesdemo1_ffmpeg_FfmpegSaveVideoUtil_stopVideo
        (JNIEnv *, jclass);

#endif