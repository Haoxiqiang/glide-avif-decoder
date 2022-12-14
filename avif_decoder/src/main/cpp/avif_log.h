#ifndef AVIF_LOG_H
#define AVIF_LOG_H

#include <android/log.h>

#define LOG_TAG "AvifDecoder"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG,__VA_ARGS__)
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#endif //AVIF_LOG_H
