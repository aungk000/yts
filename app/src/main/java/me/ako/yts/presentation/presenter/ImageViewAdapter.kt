package me.ako.yts.presentation.presenter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.google.android.material.snackbar.Snackbar
import me.ako.yts.R
import me.ako.yts.databinding.ItemImageViewBinding

class ImageViewAdapter(private val list: Array<String>) :
    RecyclerView.Adapter<ImageViewAdapter.ImageViewHolder>() {

    class ImageViewHolder(private val binding: ItemImageViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(url: String) {
            binding.apply {
                this.url = url
                zoomImg.load(url) {
                    crossfade(true)
                    error(ColorDrawable(Color.LTGRAY))
                    listener(
                        onStart = {
                            progressImageView.show()
                        },
                        onCancel = {
                            if(progressImageView.isVisible) {
                                progressImageView.hide()
                            }
                        },
                        onSuccess = { _, _ ->
                            if(progressImageView.isVisible) {
                                progressImageView.hide()
                            }
                        },
                        onError = { _, _ ->
                            if(progressImageView.isVisible) {
                                progressImageView.hide()
                            }
                            Snackbar.make(binding.root, "Error loading image", Snackbar.LENGTH_LONG)
                                .show()
                        }
                    )
                }
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemImageViewBinding.inflate(inflater, parent, false)
        return ImageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = list[position]
        holder.onBind(item)
    }
}