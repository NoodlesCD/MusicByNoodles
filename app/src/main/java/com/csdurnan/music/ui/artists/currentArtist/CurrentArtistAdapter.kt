package com.csdurnan.music.ui.artists.currentArtist

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.csdurnan.music.R
import com.csdurnan.music.dc.Album

class CurrentArtistAdapter(private val albumList: MutableList<Album>, private val fragment: Fragment) : RecyclerView.Adapter<CurrentArtistAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val albumName: TextView
        val albumInfo: TextView
        val image: ImageView
        val context: Context
        val row: View

        init {
            albumName = view.findViewById(R.id.tv_current_artist_album_title)
            albumInfo = view.findViewById(R.id.tv_current_artist_album_info)
            image = view.findViewById(R.id.iv_current_artist_album_image)
            context = view.context
            row = view.findViewById(R.id.cl_all_albums_row)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.current_artist_album_list_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    @SuppressLint("StringFormatMatches")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.albumName.text = albumList[position].title
        holder.albumInfo.text =
            holder.context.getString(R.string.artist_adapter_songs, albumList[position].songs.size)

        Glide.with(fragment)
            .load(albumList[position].albumUri)
            .placeholder(R.drawable.image)
            .into(holder.image)

        holder.row.setOnClickListener {
            val action = CurrentArtistDirections.actionCurrentArtistToCurrentAlbum(albumList[position])
            it.findNavController().navigate(action)
        }
    }
}