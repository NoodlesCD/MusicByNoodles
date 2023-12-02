package com.csdurnan.music.utils

import androidx.recyclerview.widget.DiffUtil
import com.csdurnan.music.dc.Song

object SongsListComparator : DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem == newItem
    }
}