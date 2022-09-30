# glide-avif-decoder

A avif decoder wrapper for glide.



https://user-images.githubusercontent.com/3881604/193186425-3429bde4-2fae-4e6f-8565-205130bcaf2a.mp4

### How To Use

```kotlin
// avif support.
val bitmapPool = glide.bitmapPool
val arrayPool = glide.arrayPool

registry.prepend(
	InputStream::class.java,
	AvifSequenceDrawable::class.java,
	AvifStreamSequenceDecoder(
		bitmapPool,
		arrayPool
	)
)
registry.prepend(
	ByteBuffer::class.java,
	AvifSequenceDrawable::class.java,
	AvifByteBufferSequenceDecoder(
		bitmapPool
	)
)

registry.prepend(
	Registry.BUCKET_BITMAP,
	InputStream::class.java,
	Bitmap::class.java,
	AvifStreamDecoder(
		bitmapPool,
		arrayPool
	)
)

registry.prepend(
	Registry.BUCKET_BITMAP,
	ByteBuffer::class.java,
	Bitmap::class.java,
	AvifByteBufferDecoder(bitmapPool)
)
```

### Notice
The demo use dav1d library as the decoder,you can change to libgav1/aom or other deocoders.
