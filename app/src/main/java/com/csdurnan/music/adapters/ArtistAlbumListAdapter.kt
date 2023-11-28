package com.csdurnan.music.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.csdurnan.music.R
import com.csdurnan.music.dc.Album

class ArtistAlbumListAdapter(private val albumList: ArrayList<Album>) : RecyclerView.Adapter<ArtistAlbumListAdapter.ViewHolder>() {

    /**
     * Provides a reference to the type of views that we will be using.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val albumName: TextView

        init {
            albumName = view.findViewById(R.id.tv_artist_page_row_title)
        }
    }

    /**
     * RecyclerView calls this method when it needs to create a new ViewHolder.
     * This method creates and initializes a ViewHolder and its associated view.
     * It does not fill in the view's contents with any specific data.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.artist_page_row, parent, false)
        return ViewHolder(view)
    }

    /**
     * RecyclerView calls this method to get a size of a dataset.
     * Uses this to determine when there are no more items that can be displayed.
     */
    override fun getItemCount(): Int {
        return albumList.size
    }

    /**
     * RecyclerView calls this method to associate a ViewHolder with data.
     * This method fetches appropriate data and uses it to fill in the layout.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.albumName.text = albumList[position].albumTitle
    }
}