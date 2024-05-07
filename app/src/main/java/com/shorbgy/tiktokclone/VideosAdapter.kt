package com.shorbgy.tiktokclone

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.exoplayer2.Player
import com.shorbgy.tiktokclone.databinding.ItemVideoBinding
import com.shorbgy.tiktokclone.utils.PlayerStateCallback
import com.shorbgy.tiktokclone.utils.PlayerViewAdapter.Companion.loadVideo

class VideosAdapter: ListAdapter<Video, VideosAdapter.VideoViewHolder>(COMPARATOR), PlayerStateCallback{

    companion object{
        private val COMPARATOR = object: DiffUtil.ItemCallback<Video>(){
            override fun areItemsTheSame(oldItem: Video, newItem: Video) =
                oldItem.url == newItem.url
            override fun areContentsTheSame(oldItem: Video, newItem: Video) =
                oldItem == newItem
        }
    }

    inner class VideoViewHolder(private val binding: ItemVideoBinding): ViewHolder(binding.root){
        fun bind(video: Video, position: Int){
            binding.itemVideoExoplayer.loadVideo(
                video.url,
                this@VideosAdapter,
                binding.progress,
                position,
                false
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null){
            holder.bind(currentItem, position)
        }
    }

    override fun onVideoDurationRetrieved(duration: Long, player: Player) {
    }

    override fun onVideoBuffering(player: Player) {
    }

    override fun onStartedPlaying(player: Player) {
    }

    override fun onFinishedPlaying(player: Player) {
    }
}