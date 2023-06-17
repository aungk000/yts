package me.ako.yts.presentation.presenter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import me.ako.yts.R
import me.ako.yts.data.datasource.model.MovieListEntity
import me.ako.yts.databinding.ItemMovieBinding

class MovieAdapter(
    private val onItemClicked: (MovieListEntity) -> Unit
) :
    ListAdapter<MovieListEntity, MovieAdapter.MovieViewHolder>(
        AsyncDifferConfig.Builder(DiffCallback()).build()
    ) {
    private class DiffCallback : DiffUtil.ItemCallback<MovieListEntity>() {
        override fun areItemsTheSame(oldItem: MovieListEntity, newItem: MovieListEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MovieListEntity,
            newItem: MovieListEntity
        ): Boolean {
            return oldItem == newItem
        }
    }

    class MovieViewHolder(private val binding: ItemMovieBinding, val context: Context) :
        ViewHolder(binding.root) {
        fun onBind(movie: MovieListEntity) {
            binding.apply {
                this.movie = movie

                val title = if (!movie.language.isNullOrEmpty() && movie.language != "en") {
                    val language = "[${movie.language?.uppercase()}] "
                    val span = SpannableStringBuilder()
                        .color(ContextCompat.getColor(context, R.color.primary)) {
                            append(language)
                        }
                        .append("${movie.title?.title}")
                    span.setSpan(
                        RelativeSizeSpan(0.75f),
                        0,
                        language.length,
                        SpannableString.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    span
                } else {
                    movie.title?.title
                }
                txtTitle.text = title
                imgCover.load(movie.image?.mediumCoverImage) {
                    crossfade(true)
                    error(ColorDrawable(Color.LTGRAY))
                }
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val binding = ItemMovieBinding.inflate(inflater, parent, false)
        return MovieViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
        holder.itemView.setOnClickListener {
            onItemClicked(item)
        }
    }
}