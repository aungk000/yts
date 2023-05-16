package me.ako.yts.presentation.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.ako.yts.data.network.model.Cast
import me.ako.yts.databinding.ItemCastBinding

class CastAdapter(private val list: List<Cast>, private val onClicked: (Cast) -> Unit) :
    RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

    class CastViewHolder(private val binding: ItemCastBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(cast: Cast) {
            binding.apply {
                this.cast = cast
                val name = "${cast.name} as ${cast.character_name}"
                txtActorName.text = name
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCastBinding.inflate(inflater, parent, false)
        return CastViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val item = list[position]
        holder.onBind(item)
        holder.itemView.setOnClickListener { onClicked(item) }
    }
}