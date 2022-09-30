package com.bumptech.glide.integration.avif.sample

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.avif.R

class ImageTestActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_image_test)

		val image = findViewById<AppCompatImageView>(R.id.image)

		findViewById<View>(R.id.avif).setOnClickListener {
			setImage(image, R.raw.test_avif)
		}

		findViewById<View>(R.id.avifAnimated).setOnClickListener {
			setImage(image, R.raw.test_animated_avif)
		}
	}

	private fun setImage(image: ImageView, res: Int) {
		Glide.with(image)
			.asDrawable()
			.load(res)
			.into(image)
	}
}