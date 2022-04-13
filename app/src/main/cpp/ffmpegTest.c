#include "ffmpegTest.h"
#include <android/log.h>

#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libavfilter/avfiltergraph.h"
#include "libavfilter/buffersink.h"
#include "libavfilter/buffersrc.h"
#include "libavutil/opt.h"
#include "libswresample/swresample.h"
#include "libavutil/samplefmt.h"

#define LOGI(format, ...)  __android_log_print(ANDROID_LOG_INFO,  "ffmpegtest", format, ##__VA_ARGS__)

JNIEXPORT jint JNICALL
Java_com_example_openglesdemo1_ffmpeg_FfmpegUtil_initVideo
        (JNIEnv *env, jclass cls, jstring jstring_output_path) {
    //输入地址
    const char *output_path = (*env)->GetStringUTFChars(env, jstring_output_path, 0);
    //(*env)->ReleaseStringUTFChars(env, jstring_input_path, input_path);
    LOGI("output_path= %s \n", output_path);
    (*env)->ReleaseStringUTFChars(env, jstring_output_path, output_path);
    return 0;
}

JNIEXPORT jint JNICALL
Java_com_example_openglesdemo1_ffmpeg_FfmpegUtil_stopVideo
        (JNIEnv *env, jclass cls) {
    LOGI("complete");
    return 0;
}