#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_dsoa_exynos_MainActivity_stringFromJNI(JNIEnv* env, jobject /* this */) {
    std::string hello = "DSOA EXYNOS NPU BRIDGE ACTIVE [V19.0]";
    return env->NewStringUTF(hello.c_str());
}
