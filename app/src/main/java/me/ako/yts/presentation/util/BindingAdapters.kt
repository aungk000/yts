package me.ako.yts.presentation.util

import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.google.android.material.progressindicator.CircularProgressIndicator
import me.ako.yts.data.network.ApiStatus


@BindingAdapter("loadCircleCrop")
fun loadCircleCrop(imageView: ImageView, url: String?) {
    imageView.load(url) {
        crossfade(true)
        transformations(CircleCropTransformation())
    }
}

@BindingAdapter(value = ["loadRoundedCorners", "cornerRadius"], requireAll = true)
fun loadRoundedCorners(imageView: ImageView, url: String, radius: Int) {
    imageView.load(url) {
        crossfade(true)
        transformations(RoundedCornersTransformation(radius.toFloat()))
    }
}

@BindingAdapter("number")
fun setNumber(textView: TextView, number: Any) {
    textView.text = number.toString()
}

@BindingAdapter("status")
fun bindProgressIndicator(progress: CircularProgressIndicator, status: ApiStatus?) {
    when (status) {
        is ApiStatus.Done -> {
            if(progress.isVisible) {
                progress.hide()
            }
        }

        is ApiStatus.Error -> {
            if(progress.isVisible) {
                progress.hide()
            }
        }

        is ApiStatus.Loading -> {
            progress.show()
        }

        else -> {
            if(progress.isVisible) {
                progress.hide()
            }
        }
    }
}

@BindingAdapter("status")
fun bindSwipeRefresh(swipeRefresh: SwipeRefreshLayout, status: ApiStatus?) {
    when (status) {
        is ApiStatus.Done -> {
            if (swipeRefresh.isRefreshing) {
                swipeRefresh.isRefreshing = false
            }
        }

        is ApiStatus.Error -> {
            if (swipeRefresh.isRefreshing) {
                swipeRefresh.isRefreshing = false
            }
        }

        is ApiStatus.Loading -> {
            if (!swipeRefresh.isRefreshing) {
                swipeRefresh.isRefreshing = true
            }
        }

        else -> {
            if (swipeRefresh.isRefreshing) {
                swipeRefresh.isRefreshing = false
            }
        }
    }
}