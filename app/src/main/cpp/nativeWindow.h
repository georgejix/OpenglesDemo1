#ifndef COM_EXAMPLE_OPENGLESDEMO1_NATIVEGL
#define COM_EXAMPLE_OPENGLESDEMO1_NATIVEGL

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL drawColor(JNIEnv *, jobject, jobject, jint);

JNIEXPORT void JNICALL drawBitmap(JNIEnv *, jobject, jobject, jobject);

#ifdef __cplusplus
}
#endif
#endif