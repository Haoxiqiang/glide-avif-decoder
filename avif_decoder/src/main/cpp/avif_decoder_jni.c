#include <jni.h>
#include <stdio.h>
#include <malloc.h>
#include <android/bitmap.h>

#include "avif/avif.h"

#include "avif_util.h"
#include "avif_log.h"

typedef struct {
    jbyte *bytes;
    //对应java的byte[]
    jobjectArray byteArrayRef;
    //ByteBuffer对象
    jobject byteBufferRef;

} AvifByteBuffer;

typedef struct {
    avifDecoder *decoder;
    AvifByteBuffer buffer;
} AvifImage;

JNIEXPORT jlong JNICALL
Java_com_bumptech_glide_integration_avif_decoder_AvifDecoder_createDecoderByteArray0(
        JNIEnv *env,
        jclass clazz,
        jbyteArray byteArray,
        jint off,
        jint len) {

    UNUSED(clazz);

    AvifImage *avifImage = malloc(sizeof(AvifImage));

    avifDecoder *decoder = avifDecoderCreate();
    avifImage->decoder = decoder;
    avifImage->buffer.bytes = (*env)->GetByteArrayElements(env, byteArray, NULL);
    avifImage->buffer.byteArrayRef = (*env)->NewGlobalRef(env, byteArray);
    avifImage->buffer.byteBufferRef = NULL;

    avifResult result = avifDecoderSetIOMemory(decoder,
                                               (const uint8_t *) (avifImage->buffer.bytes + off),
                                               len);
    if (result != AVIF_RESULT_OK) {
        const char *m = avifResultToString(result);
        LOGE("avif setIOMemory failed.%s", m);
        goto cleanup;
    }

    result = avifDecoderParse(decoder);
    if (result != AVIF_RESULT_OK) {
        const char *m = avifResultToString(result);
        LOGE("avif avifDecoderParse failed.%s", m);
        goto cleanup;
    }

    return ptr_to_jlong(avifImage);

    // cleanup avifImage data.
    cleanup:
    avifDecoderDestroy(decoder);
    (*env)->ReleaseByteArrayElements(
            env,
            avifImage->buffer.byteArrayRef,
            avifImage->buffer.bytes,
            JNI_ABORT);
    (*env)->DeleteGlobalRef(env, avifImage->buffer.byteArrayRef);
    free(avifImage);
    return 0;
}

JNIEXPORT jlong JNICALL
Java_com_bumptech_glide_integration_avif_decoder_AvifDecoder_createDecoderByteBuffer0(
        JNIEnv *env,
        jclass clazz,
        jobject byteBuffer,
        jint off,
        jint len) {

    UNUSED(clazz);

    AvifImage *avifImage = malloc(sizeof(AvifImage));

    avifDecoder *decoder = avifDecoderCreate();
    avifImage->decoder = decoder;
    avifImage->buffer.bytes = (*env)->GetDirectBufferAddress(env, byteBuffer);
    avifImage->buffer.byteBufferRef = (*env)->NewGlobalRef(env, byteBuffer);
    avifImage->buffer.byteArrayRef = NULL;

    avifResult result = avifDecoderSetIOMemory(decoder,
                                               (const uint8_t *) (avifImage->buffer.bytes + off),
                                               len);
    if (result != AVIF_RESULT_OK) {
        const char *m = avifResultToString(result);
        LOGE("avif setIOMemory failed.%s", m);
        jniThrowException(env, ILLEGAL_STATE_EXCEPTION, m);
        goto cleanup;
    }
    result = avifDecoderParse(decoder);
    if (result != AVIF_RESULT_OK) {
        const char *m = avifResultToString(result);
        LOGE("avif avifDecoderParse failed.%s", m);
        jniThrowException(env, ILLEGAL_STATE_EXCEPTION, m);
        goto cleanup;
    }

    return ptr_to_jlong(avifImage);

    cleanup:
    avifDecoderDestroy(decoder);
    (*env)->DeleteGlobalRef(env, avifImage->buffer.byteBufferRef);
    free(avifImage);
    return 0;
}

JNIEXPORT jint JNICALL
Java_com_bumptech_glide_integration_avif_decoder_AvifDecoder_getImageCount0(
        JNIEnv *env,
        jclass clazz,
        jlong n_decoder) {

    UNUSED(env);
    UNUSED(clazz);

    AvifImage *avifImage = jlong_to_ptr(n_decoder);
    return avifImage->decoder->imageCount;
}

JNIEXPORT jint JNICALL
Java_com_bumptech_glide_integration_avif_decoder_AvifDecoder_getImageIndex0(
        JNIEnv *env,
        jclass clazz,
        jlong n_decoder) {

    UNUSED(env);
    UNUSED(clazz);

    AvifImage *avifImage = jlong_to_ptr(n_decoder);
    return avifImage->decoder->imageIndex;
}

JNIEXPORT jint JNICALL
Java_com_bumptech_glide_integration_avif_decoder_AvifDecoder_getImageLimit0(
        JNIEnv *env,
        jclass clazz,
        jlong n_decoder) {

    UNUSED(env);
    UNUSED(clazz);

    AvifImage *avifImage = jlong_to_ptr(n_decoder);
    return (int32_t) avifImage->decoder->imageCountLimit;
}

JNIEXPORT jboolean JNICALL
Java_com_bumptech_glide_integration_avif_decoder_AvifDecoder_nextImage0(
        JNIEnv *env,
        jclass clazz,
        jlong n_decoder) {

    UNUSED(env);
    UNUSED(clazz);

    AvifImage *avifImage = jlong_to_ptr(n_decoder);
    return avifDecoderNextImage(avifImage->decoder) == AVIF_RESULT_OK;
}

JNIEXPORT jlong JNICALL
Java_com_bumptech_glide_integration_avif_decoder_AvifDecoder_getImage0(
        JNIEnv *env,
        jclass clazz,
        jlong n_decoder) {

    UNUSED(env);
    UNUSED(clazz);

    AvifImage *avifImage = jlong_to_ptr(n_decoder);
    return ptr_to_jlong(avifImage->decoder->image);
}

JNIEXPORT jlong JNICALL
Java_com_bumptech_glide_integration_avif_decoder_AvifDecoder_getFrame0(
        JNIEnv *env,
        jclass clazz,
        jlong n_decoder,
        jobject bitmap) {

    UNUSED(clazz);

    AvifImage *avifImage = jlong_to_ptr(n_decoder);

    int ret;

    avifResult result;
    AndroidBitmapInfo info;
    void *pixels;
    avifRGBImage rgb;

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0
        || info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Couldn't get info from Bitmap, error:%d", ret);
        jniThrowException(env, ILLEGAL_STATE_EXCEPTION, "Couldn't get info from Bitmap.");
        return 0L;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGE("Bitmap pixels couldn't be locked, error:%d", ret);
        jniThrowException(env, ILLEGAL_STATE_EXCEPTION, "Bitmap pixels couldn't be locked.");
        return 0L;
    }

    avifRGBImageSetDefaults(&rgb, avifImage->decoder->image);

    rgb.format = AVIF_RGB_FORMAT_RGBA;
    rgb.depth = 8;
    rgb.rowBytes = rgb.width * avifRGBImagePixelSize(&rgb);
    rgb.pixels = pixels;

    result = avifImageYUVToRGB(avifImage->decoder->image, &rgb);

    AndroidBitmap_unlockPixels(env, bitmap);

    if (result != AVIF_RESULT_OK) {
        LOGE("avifImageYUVToRGB failed. error:%s", avifResultToString(result));
        jniThrowException(env, ILLEGAL_STATE_EXCEPTION, avifResultToString(result));
    }

    //ms
    return (long) (avifImage->decoder->imageTiming.duration * 1000L);
}

JNIEXPORT void JNICALL
Java_com_bumptech_glide_integration_avif_decoder_AvifDecoder_reset0(
        JNIEnv *env,
        jclass clazz,
        jlong n_decoder) {

    UNUSED(clazz);

    AvifImage *avifImage = jlong_to_ptr(n_decoder);
    avifResult result = avifDecoderReset(avifImage->decoder);
    if (result != AVIF_RESULT_OK) {
        LOGE("avifDecoderReset failed. error:%s", avifResultToString(result));
        jniThrowException(env, ILLEGAL_STATE_EXCEPTION, avifResultToString(result));
    }
}

JNIEXPORT void JNICALL
Java_com_bumptech_glide_integration_avif_decoder_AvifDecoder_destroy0(
        JNIEnv *env,
        jclass clazz,
        jlong n_decoder) {

    UNUSED(clazz);

    AvifImage *avifImage = jlong_to_ptr(n_decoder);

    //free byte array
    if (avifImage->buffer.byteArrayRef != NULL) {
        (*env)->ReleaseByteArrayElements(
                env,
                avifImage->buffer.byteArrayRef,
                avifImage->buffer.bytes,
                JNI_ABORT);
        (*env)->DeleteGlobalRef(env, avifImage->buffer.byteArrayRef);
    }
    //free byte buffer
    if (avifImage->buffer.byteBufferRef != NULL) {
        (*env)->DeleteGlobalRef(env, avifImage->buffer.byteBufferRef);
    }
    avifDecoderDestroy(avifImage->decoder);

    free(avifImage);
}
