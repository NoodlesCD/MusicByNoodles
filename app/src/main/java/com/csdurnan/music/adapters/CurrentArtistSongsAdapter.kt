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
import com.csdurnan.music.dc.Song
import com.csdurnan.music.fragments.CurrentArtistDirections

class CurrentArtistSongsAdapter(private val songs: MutableList<Song>, private val fragment: Fragment) : RecyclerView.Adapter<CurrentArtistSongsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songIndex: TextView
        val songTitle: TextView
        val row: ConstraintLayout

        init {
            songIndex = view.findViewById(R.id.tv_current_artist_album_list_index)
            songTitle = view.findViewById(R.id.tv_current_artist_album_list_title)
            row = view.findViewById(R.id.cl_current_album_song_row_recycler)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.current_artist_album_list_row_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.songIndex.text = (position + 1).toString()
        holder.songTitle.text = songs[position].title

        holder.row.setOnClickListener {
            val action = CurrentArtistDirections.actionGlobalCurrentSong(songs[position].id)
            it.findNavController().navigate(action)
        }
    }
}
