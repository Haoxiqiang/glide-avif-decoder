#ifndef AVIF_UTIL_H
#define AVIF_UTIL_H

#include <jni.h>

#ifdef _LP64
#define jlong_to_ptr(a) ((void*)(a))
#define ptr_to_jlong(a) ((jlong)(a))
#else
#define jlong_to_ptr(a) ((void*)(int)(a))
#define ptr_to_jlong(a) ((jlong)(int)(a))
#endif

#define UNUSED(x) (void)(x)

#define ILLEGAL_STATE_EXCEPTION "java/lang/IllegalStateException"

void jniThrowException(JNIEnv *env, const char *className, const char *msg);

#endif //AVIF_UTIL_H
