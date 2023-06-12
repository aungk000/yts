package me.ako.yts.presentation.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import me.ako.yts.R
import me.ako.yts.data.network.model.Torrent
import me.ako.yts.databinding.ItemTorrentBinding

class TorrentAdapter(private val list: List<Torrent>) :
    RecyclerView.Adapter<TorrentAdapter.TorrentViewHolder>() {

    class TorrentViewHolder(private val binding: ItemTorrentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(torrent: Torrent) {
            binding.apply {
                this.torrent = torrent
                val type = torrent.type.replaceFirstChar { it.uppercase() }
                txtTorrentType.text = type
                val peers = "Peers: ${torrent.peers}"
                txtTorrentPeers.text = peers
                val seeds = "Seeds: ${torrent.seeds}"
                txtTorrentSeeds.text = seeds
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TorrentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTorrentBinding.inflate(inflater, parent, false)
        return TorrentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TorrentViewHolder, position: Int) {
        val item = list[position]
        holder.onBind(item)
    }
}