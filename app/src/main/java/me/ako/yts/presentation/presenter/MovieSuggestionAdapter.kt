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
import me.ako.yts.data.network.model.MovieSuggestion
import me.ako.yts.databinding.ItemMovieSuggestionBinding

class MovieSuggestionAdapter(private val onClicked: (MovieSuggestion) -> Unit) :
    ListAdapter<MovieSuggestion, MovieSuggestionAdapter.MovieSuggestionViewHolder>(
        AsyncDifferConfig.Builder(DiffCallback()).build()
    ) {
    private class DiffCallback : DiffUtil.ItemCallback<MovieSuggestion>() {
        override fun areItemsTheSame(oldItem: MovieSuggestion, newItem: MovieSuggestion): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: MovieSuggestion, newItem: MovieSuggestion): Boolean {
            return oldItem == newItem
        }
    }

    class MovieSuggestionViewHolder(private val binding: ItemMovieSuggestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(movie: MovieSuggestion) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieSuggestionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMovieSuggestionBinding.inflate(inflater, parent, false)
        return MovieSuggestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieSuggestionViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
        holder.itemView.setOnClickListener { onClicked(item) }
    }
}