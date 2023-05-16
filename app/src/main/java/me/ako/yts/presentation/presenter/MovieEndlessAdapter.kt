package me.ako.yts.presentation.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.ako.yts.data.datasource.model.MovieEntity
import me.ako.yts.databinding.ItemMovieBinding
import me.ako.yts.databinding.ItemProgressBinding


class MovieEndlessAdapter(val onClicked: (MovieEntity) -> Unit) :
    ListAdapter<MovieEntity?, RecyclerView.ViewHolder>(
        AsyncDifferConfig.Builder(DiffCallback()).build()
    ) {
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    private class DiffCallback : DiffUtil.ItemCallback<MovieEntity>() {
        override fun areItemsTheSame(oldItem: MovieEntity, newItem: MovieEntity): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MovieEntity, newItem: MovieEntity): Boolean {
            return oldItem != newItem
        }
    }

    private inner class ItemViewHolder(val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(movie: MovieEntity) {
            binding.apply {
                this.movie = movie
                txtYear.text = movie.year.toString()
                executePendingBindings()
            }
        }
    }

    private inner class LoadingViewHolder(val binding: ItemProgressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind() {
            binding.progressMore.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding = ItemMovieBinding.inflate(inflater, parent, false)
            ItemViewHolder(binding)
        } else {
            val binding = ItemProgressBinding.inflate(inflater, parent, false)
            LoadingViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (holder is ItemViewHolder) {
            holder.onBind(item!!)
            holder.itemView.setOnClickListener {
                onClicked(item)
            }
        } else if (holder is LoadingViewHolder) {
            holder.onBind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }
}