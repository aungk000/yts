package me.ako.yts.presentation.presenter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import me.ako.yts.R
import me.ako.yts.data.network.model.MovieList
import me.ako.yts.databinding.ItemSearchBinding

class SearchAdapter(private val onClicked: (MovieList) -> Unit) :
    ListAdapter<MovieList, SearchAdapter.SearchViewHolder>(
        AsyncDifferConfig.Builder(DiffCallback()).build()
    ) {
    private class DiffCallback : DiffUtil.ItemCallback<MovieList>() {
        override fun areItemsTheSame(oldItem: MovieList, newItem: MovieList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieList, newItem: MovieList): Boolean {
            return oldItem == newItem
        }
    }

    class SearchViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(movie: MovieList) {
            binding.apply {
                this.movie = movie
                imgCover.load(movie.medium_cover_image) {
                    crossfade(true)
                    error(ColorDrawable(Color.LTGRAY))
                }
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchBinding.inflate(inflater, parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
        holder.itemView.setOnClickListener { onClicked(item) }
    }
}