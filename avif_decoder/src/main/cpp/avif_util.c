#include "avif_util.h"
#include "avif_log.h"
#include <stdlib.h>

void jniThrowException(JNIEnv *env, const char *className, const char *msg) {
    jclass clazz = (*env)->FindClass(env, className);
    if (!clazz) {
        LOGE("Unable to find exception class %s", className);
        return;
    }

    if ((*env)->ThrowNew(env, clazz, msg) != JNI_OK) {
        LOGE("Failed throwing '%s' '%s'", className, msg);
        /* an exception, most likely OOM, will now be pending */
    }
    (*env)->DeleteLocalRef(env, clazz);
}
