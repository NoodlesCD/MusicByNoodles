package com.csdurnan.music.ui.songs

import androidx.recyclerview.widget.DiffUtil
import com.csdurnan.music.dc.Song

object AllSongsListComparator : DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem == newItem
    }
}