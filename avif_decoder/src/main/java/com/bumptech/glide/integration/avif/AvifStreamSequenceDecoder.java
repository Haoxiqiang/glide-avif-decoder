package com.bumptech.glide.integration.avif;


import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.integration.avif.decoder.AvifDecoder;
import com.bumptech.glide.integration.avif.decoder.AvifImageDetector;
import com.bumptech.glide.integration.avif.decoder.AvifSequenceDrawable;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AvifStreamSequenceDecoder
        implements ResourceDecoder<InputStream, AvifSequenceDrawable> {

    @NonNull private final ArrayPool byteArrayPool;
    private final AvifSequenceDrawable.BitmapProvider bitmapProvider;

    public AvifStreamSequenceDecoder(
            @NonNull final BitmapPool bitmapPool, @NonNull ArrayPool byteArrayPool) {
        this.byteArrayPool = byteArrayPool;
        bitmapProvider =
                new AvifSequenceDrawable.BitmapProvider() {
                    @Override
                    public Bitmap acquireBitmap(int minWidth, int minHeight) {
                        return bitmapPool.getDirty(minWidth, minHeight, Bitmap.Config.ARGB_8888);
                    }

                    @Override
                    public void releaseBitmap(Bitmap bitmap) {
                        bitmapPool.put(bitmap);
                    }
                };
    }

    @Override
    public boolean handles(@NonNull InputStream source, @NonNull Options options) {
        if (Boolean.TRUE.equals(options.get(AvifOptions.DISABLE_ANIMATION))) {
            return false;
        }
        return AvifImageDetector.isAvifs(source);
    }

    @Nullable
    @Override
    public Resource<AvifSequenceDrawable> decode(
            @NonNull InputStream source, int width, int height, @NonNull Options options) {

        final byte[] bytes = inputStreamToBytes(source);
        if (bytes == null) {
            return null;
        }
        final AvifDecoder avifDecoder = AvifDecoder.fromByteArray(bytes);
        if (avifDecoder == null) {
            return null;
        }

        final AvifSequenceDrawable drawable = new AvifSequenceDrawable(avifDecoder, bitmapProvider);
        if (Boolean.TRUE.equals(options.get(AvifOptions.LOOP_ONCE))) {
            drawable.setLoopBehavior(AvifSequenceDrawable.LOOP_DEFAULT);
            drawable.setLoopCount(1);
        } else {
            drawable.setLoopBehavior(AvifSequenceDrawable.LOOP_INF);
        }

        return new AvifSequenceDrawableResource(drawable);
    }

    private byte[] inputStreamToBytes(InputStream is) {
        final int bufferSize = 16 * 1024;
        int dataLen;
        try {
            dataLen = is.available();
        } catch (IOException ignored) {
            dataLen = bufferSize;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(dataLen);
        try {
            int nRead;
            byte[] data = byteArrayPool.get(bufferSize, byte[].class);
            while ((nRead = is.read(data)) != -1) {
                buffer.write(data, 0, nRead);
            }
            byteArrayPool.put(data);
            buffer.close();
        } catch (IOException e) {
            return null;
        }
        return buffer.toByteArray();
    }
}
