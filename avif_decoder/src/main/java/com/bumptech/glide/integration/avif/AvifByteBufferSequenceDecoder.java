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
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.nio.ByteBuffer;

public class AvifByteBufferSequenceDecoder
        implements ResourceDecoder<ByteBuffer, AvifSequenceDrawable> {

    private final AvifSequenceDrawable.BitmapProvider bitmapProvider;

    public AvifByteBufferSequenceDecoder(@NonNull final BitmapPool bitmapPool) {
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
    public boolean handles(@NonNull ByteBuffer source, @NonNull Options options) {
        if (Boolean.TRUE.equals(options.get(AvifOptions.DISABLE_ANIMATION))) {
            return false;
        }
        return AvifImageDetector.isAvifs(source);
    }

    @Nullable
    @Override
    public Resource<AvifSequenceDrawable> decode(
            @NonNull ByteBuffer source, int width, int height, @NonNull Options options) {

        final AvifDecoder avifDecoder = AvifDecoder.fromByteBuffer(source);
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
}
