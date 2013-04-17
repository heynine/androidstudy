#include <jni.h>
extern "C" {

jstring Java_com_example_demo_MainActivity_say(JNIEnv* env, jobject thiz) {
	return env->NewStringUTF("nihao");
}

jstring Java_com_example_demo_MainActivity_stringFromJNI(JNIEnv* env,
		jobject thiz) {
	return env->NewStringUTF("Hello World");
}

}
