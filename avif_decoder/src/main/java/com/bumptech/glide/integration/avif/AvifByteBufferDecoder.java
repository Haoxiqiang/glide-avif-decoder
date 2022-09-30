package com.bumptech.glide.integration.avif;


import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.integration.avif.decoder.AvifDecoder;
import com.bumptech.glide.integration.avif.decoder.AvifImage;
import com.bumptech.glide.integration.avif.decoder.AvifImageDetector;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.nio.ByteBuffer;

public class AvifByteBufferDecoder implements ResourceDecoder<ByteBuffer, Bitmap> {

    @NonNull private final BitmapPool bitmapPool;

    public AvifByteBufferDecoder(@NonNull final BitmapPool bitmapPool) {
        this.bitmapPool = bitmapPool;
    }

    @Override
    public boolean handles(@NonNull ByteBuffer source, @NonNull Options options) {
        return AvifImageDetector.isAvif(source);
    }

    @Nullable
    @Override
    public Resource<Bitmap> decode(
            @NonNull ByteBuffer source, int width, int height, @NonNull Options options) {
        final AvifDecoder decoder = AvifDecoder.fromByteBuffer(source);
        if (decoder == null) {
            return null;
        }
        if (!decoder.nextImage()) {
            return null;
        }

        final AvifImage image = decoder.getImage();
        final Bitmap bitmap =
                bitmapPool.getDirty(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        decoder.getFrame(bitmap);

        decoder.destroy();

        return new AvifBitmapResource(bitmapPool, bitmap);
    }
}
