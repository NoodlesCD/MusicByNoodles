package com.csdurnan.music.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.csdurnan.music.adapters.AllSongsAdapter
import com.csdurnan.music.dc.Song

class AllSongsPagingAdapter(diffCallback: DiffUtil.ItemCallback<Song>, var onSongsItemClickListener: AllSongsAdapter.OnSongsItemClickListener) :
    PagingDataAdapter<Song, AllSongsViewHolder>(diffCallback){

    override fun onBindViewHolder(holder: AllSongsViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllSongsViewHolder {
        return AllSongsViewHolder(parent, onSongsItemClickListener)
    }
}