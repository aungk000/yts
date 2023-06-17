package me.ako.yts.presentation.presenter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import me.ako.yts.databinding.ItemScreenshotBinding


class ScreenshotAdapter(private val list: List<String?>, private val onItemClicked: (String?) -> Unit) :
    RecyclerView.Adapter<ScreenshotAdapter.ScreenshotViewHolder>() {

    class ScreenshotViewHolder(private val binding: ItemScreenshotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(url: String?) {
            binding.apply {
                imgScreenshot.load(url) {
                    crossfade(true)
                    error(ColorDrawable(Color.LTGRAY))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScreenshotViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemScreenshotBinding.inflate(inflater, parent, false)
        return ScreenshotViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ScreenshotViewHolder, position: Int) {
        val item = list[position]
        holder.onBind(item)
        holder.itemView.setOnClickListener {
            onItemClicked(item)
        }
    }
}