package com.csdurnan.music.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.bumptech.glide.Glide
import com.csdurnan.music.R
import com.csdurnan.music.dc.Album

class CurrentArtistAdapter(private val albumList: MutableList<Album>, private val fragment: Fragment) : RecyclerView.Adapter<CurrentArtistAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val albumName: TextView
        val albumInfo: TextView
        val songsList: RecyclerView
        val image: ImageView
        val context: Context

        init {
            albumName = view.findViewById(R.id.tv_current_artist_album_title)
            albumInfo = view.findViewById(R.id.tv_current_artist_album_info)
            songsList = view.findViewById(R.id.rv_current_artist_album_recycler)
            image = view.findViewById(R.id.iv_current_artist_album_image)
            context = view.context
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.current_artist_album_list_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.albumName.text = albumList[position].albumTitle
        holder.albumInfo.text = albumList[position].songs.size.toString() + " songs"

        var songsList = holder.songsList
        songsList.layoutManager = LinearLayoutManager(holder.context)
        songsList.setHasFixedSize(true)
        songsList.adapter = holder.context?.let { CurrentArtistSongsAdapter(albumList[position].songs, fragment) }

        Glide.with(fragment)
            .load(albumList[position].albumUri)
            .placeholder(R.drawable.image)
            .into(holder.image)
    }
}