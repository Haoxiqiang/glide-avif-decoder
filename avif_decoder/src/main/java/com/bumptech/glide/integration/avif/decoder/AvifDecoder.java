package com.bumptech.glide.integration.avif.decoder;


import android.graphics.Bitmap;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import java.nio.ByteBuffer;

@Keep
public class AvifDecoder {

    static {
        try {
            System.loadLibrary("avif_android");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private AvifDecoder(long nDecoder) {
        this.nDecoder = nDecoder;
    }

    private volatile long nDecoder;

    public static AvifDecoder fromByteArray(byte[] bytes) {
        return fromByteArray(bytes, 0, bytes.length);
    }

    public static AvifDecoder fromByteArray(byte[] bytes, int off, int len) {
        final long nDecoder = createDecoderByteArray0(bytes, off, len);
        if (nDecoder == 0) {
            return null;
        }
        return new AvifDecoder(nDecoder);
    }

    public static AvifDecoder fromByteBuffer(ByteBuffer buffer) throws IllegalArgumentException {
        if (buffer == null) throw new IllegalArgumentException("buffer==null");
        if (!buffer.isDirect()) {
            if (buffer.hasArray()) {
                byte[] byteArray = buffer.array();
                return fromByteArray(byteArray, buffer.position(), buffer.remaining());
            } else {
                throw new IllegalArgumentException(
                        "Cannot have non-direct ByteBuffer with no byte array");
            }
        }
        final long nDecoder =
                createDecoderByteBuffer0(buffer, buffer.position(), buffer.remaining());
        if (nDecoder == 0) {
            return null;
        }
        return new AvifDecoder(nDecoder);
    }

    public boolean nextImage() throws IllegalStateException {
        checkDecoder();
        return nextImage0(nDecoder);
    }

    @NonNull
    public AvifImage getImage() throws IllegalStateException {
        checkDecoder();
        return new AvifImage(getImage0(nDecoder));
    }

    @NonNull
    public Bitmap getFrame() throws IllegalStateException {
        checkDecoder();
        final AvifImage image = getImage();
        final Bitmap bitmap =
                Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        getFrame0(nDecoder, bitmap);
        return bitmap;
    }

    public long getFrame(@NonNull Bitmap bitmap) throws IllegalStateException {
        checkDecoder();
        return getFrame0(nDecoder, bitmap);
    }

    public int getImageCount() throws IllegalStateException {
        checkDecoder();
        return getImageCount0(nDecoder);
    }

    public int getImageIndex() throws IllegalStateException {
        checkDecoder();
        return getImageIndex0(nDecoder);
    }

    public void reset() throws IllegalStateException {
        checkDecoder();
        reset0(nDecoder);
    }

    private void checkDecoder() throws IllegalStateException {
        if (nDecoder == 0) {
            throw new IllegalStateException("Native Decoder already destroyed");
        }
    }

    public void destroy() {
        synchronized (this) {
            if (nDecoder != 0) {
                destroy0(nDecoder);
                nDecoder = 0;
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            destroy();
        } finally {
            super.finalize();
        }
    }

    private static native long createDecoderByteArray0(@NonNull byte[] bytes, int off, int len);

    private static native long createDecoderByteBuffer0(
            @NonNull ByteBuffer buffer, int off, int len);

    private static native boolean nextImage0(long nDecoder);

    private static native int getImageCount0(long nDecoder);

    private static native int getImageIndex0(long nDecoder);

    private static native int getImageLimit0(long nDecoder);

    private static native long getImage0(long nDecoder);

    private static native long getFrame0(long nDecoder, @NonNull Bitmap outBitmap);

    private static native void reset0(long nDecoder);

    private static native void destroy0(long nDecoder);
}
