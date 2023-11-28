package com.csdurnan.music.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.csdurnan.music.R
import com.csdurnan.music.dc.Album
import com.csdurnan.music.dc.Song
import com.csdurnan.music.fragments.CurrentAlbumDirections

class CurrentAlbumAdapter(private val currentAlbum: Album, private val fragment: Fragment) : RecyclerView.Adapter<CurrentAlbumAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val index: TextView
        val songName: TextView
        val row: ConstraintLayout

        init {
            index = view.findViewById(R.id.tv_current_album_list_index)
            songName = view.findViewById(R.id.tv_current_album_list_title)
            row = view.findViewById(R.id.cl_current_album_song_row)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.current_album_list_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return currentAlbum.songs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.index.text = (position + 1).toString()
        holder.songName.text = currentAlbum.songs[position].title
        holder.row.setOnClickListener {
            val action =
                CurrentAlbumDirections.actionGlobalCurrentSong(currentAlbum.songs[position].id)
            it.findNavController().navigate(action)
        }
    }
}