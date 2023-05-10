package me.ako.yts.presentation.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import me.ako.yts.R


@BindingAdapter("load")
fun bindLoadImage(imageView: ImageView, image: String?) {
    image?.let {
        imageView.load(it) {
        }
    }
}