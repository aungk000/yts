package me.ako.yts.presentation.util

import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import com.google.android.material.progressindicator.CircularProgressIndicator
import me.ako.yts.R
import me.ako.yts.data.network.MovieApi


@BindingAdapter("load")
fun bindLoadImage(imageView: ImageView, url: String?) {
    imageView.load(url)
}

@BindingAdapter("status")
fun bindProgressIndicator(progress: CircularProgressIndicator, status: MovieApi.ApiStatus?) {
    when (status) {
        is MovieApi.ApiStatus.Done -> {
            progress.hide()
        }

        is MovieApi.ApiStatus.Error -> {
            progress.hide()
        }

        is MovieApi.ApiStatus.Loading -> {
            progress.show()
        }

        else -> {
            progress.hide()
        }
    }
}

@BindingAdapter("status")
fun bindSwipeRefresh(swipeRefresh: SwipeRefreshLayout, status: MovieApi.ApiStatus?) {
    when (status) {
        is MovieApi.ApiStatus.Done -> {
            if (swipeRefresh.isRefreshing) {
                swipeRefresh.isRefreshing = false
            }
        }

        is MovieApi.ApiStatus.Error -> {
            if (swipeRefresh.isRefreshing) {
                swipeRefresh.isRefreshing = false
            }
        }

        is MovieApi.ApiStatus.Loading -> {
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