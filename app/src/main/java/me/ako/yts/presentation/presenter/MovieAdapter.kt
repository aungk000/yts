package me.ako.yts.presentation.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import me.ako.yts.R
import me.ako.yts.data.datasource.model.MovieEntity
import me.ako.yts.databinding.ItemMovieBinding

class MovieAdapter(
    private val onItemClicked: (MovieEntity) -> Unit
) :
    ListAdapter<MovieEntity, MovieAdapter.MovieViewHolder>(
        AsyncDifferConfig.Builder(DiffCallback()).build()
    ) {
    private class DiffCallback : DiffUtil.ItemCallback<MovieEntity>() {
        override fun areItemsTheSame(oldItem: MovieEntity, newItem: MovieEntity): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: MovieEntity, newItem: MovieEntity): Boolean {
            return oldItem == newItem
        }
    }

    class MovieViewHolder(private val binding: ItemMovieBinding) : ViewHolder(binding.root) {
        fun onBind(movie: MovieEntity) {
            binding.apply {
                this.movie = movie
                imgCover.load(movie.medium_cover_image) {
                    error(R.drawable.no_poster)
                }
                txtYear.text = movie.year?.toString()
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMovieBinding.inflate(inflater, parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
        holder.itemView.setOnClickListener {
            onItemClicked(item)
        }
    }
}