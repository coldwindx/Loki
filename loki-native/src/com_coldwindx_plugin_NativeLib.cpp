#include "com_coldwindx_plugin_NativeLib.h"
JNIEXPORT jint JNICALL Java_com_coldwindx_plugin_NativeLib_add(JNIEnv * env, jobject obj, jint a, jint b)
{
	return a + b;
}