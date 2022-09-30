package com.bumptech.glide.integration.avif.sample

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.avif.AvifByteBufferDecoder
import com.bumptech.glide.integration.avif.AvifByteBufferSequenceDecoder
import com.bumptech.glide.integration.avif.AvifStreamDecoder
import com.bumptech.glide.integration.avif.AvifStreamSequenceDecoder
import com.bumptech.glide.integration.avif.decoder.AvifSequenceDrawable
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream
import java.nio.ByteBuffer

@GlideModule
class AppGlide : AppGlideModule() {

	companion object {
		const val diskCacheSizeBytes = 1024 * 1024 * 100L // 100 MB
	}

	override fun applyOptions(context: Context, builder: GlideBuilder) {
		super.applyOptions(context, builder)
		builder.setLogRequestOrigins(true)
		builder.setDiskCache(
			ExternalPreferredCacheDiskCacheFactory(
				context,
				"image_disk_cache",
				diskCacheSizeBytes
			)
		)
	}

	override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
		super.registerComponents(context, glide, registry)
		//registry.register(
		//	SVG::class.java,
		//	PictureDrawable::class.java,
		//	SvgDrawableTranscoder()
		//).append(InputStream::class.java, SVG::class.java, SvgDecoder())

		//registry
		//	.prepend(
		//		InputStream::class.java,
		//		Apng::class.java,
		//		ApngInputStreamDecoder()
		//	)

		//registry.register(
		//	Apng::class.java,
		//	ApngDrawable::class.java,
		//	ApngDrawableTranscoder()
		//)

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
	}

	override fun isManifestParsingEnabled(): Boolean {
		return false
	}
}