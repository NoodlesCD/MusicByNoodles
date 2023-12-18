package com.csdurnan.music.ui.songs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.csdurnan.music.R
import com.csdurnan.music.dc.Song

class AllSongsPagingAdapter(var onSongsItemClickListener: AllSongsAdapter.OnSongsItemClickListener) :
    PagingDataAdapter<Song, AllSongsViewHolder>(AllSongsListComparator){

    override fun onBindViewHolder(holder: AllSongsViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllSongsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.songs_all_list_row, parent, false)
        return AllSongsViewHolder(view, onSongsItemClickListener)
    }

}