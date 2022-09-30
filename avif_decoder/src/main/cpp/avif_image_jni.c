#include <jni.h>
#include "avif/avif.h"
#include "avif_util.h"

JNIEXPORT jint JNICALL
Java_com_bumptech_glide_integration_avif_decoder_AvifImage_getWidth0(JNIEnv *env, jclass clazz,
                                                                     jlong n_image) {
    UNUSED(env);
    UNUSED(clazz);

    avifImage *image = jlong_to_ptr(n_image);
    return (int32_t) image->width;
}

JNIEXPORT jint JNICALL
Java_com_bumptech_glide_integration_avif_decoder_AvifImage_getHeight0(JNIEnv *env, jclass clazz,
                                                                      jlong n_image) {
    UNUSED(env);
    UNUSED(clazz);

    avifImage *image = jlong_to_ptr(n_image);
    return (int32_t) image->height;
}

JNIEXPORT jint JNICALL
Java_com_bumptech_glide_integration_avif_decoder_AvifImage_getDepth0(JNIEnv *env, jclass clazz,
                                                                     jlong n_image) {
    UNUSED(env);
    UNUSED(clazz);

    avifImage *image = jlong_to_ptr(n_image);
    return (int32_t) image->depth;
}

JNIEXPORT jint JNICALL
Java_com_bumptech_glide_integration_avif_decoder_AvifImage_getYuvFormat0(JNIEnv *env, jclass clazz,
                                                                         jlong n_image) {
    UNUSED(env);
    UNUSED(clazz);

    avifImage *image = jlong_to_ptr(n_image);
    return (int32_t) image->yuvFormat;
}

JNIEXPORT jint JNICALL
Java_com_bumptech_glide_integration_avif_decoder_AvifImage_getYuvRange0(JNIEnv *env, jclass clazz,
                                                                        jlong n_image) {
    UNUSED(env);
    UNUSED(clazz);

    avifImage *image = jlong_to_ptr(n_image);
    return (int32_t) image->yuvRange;
}
