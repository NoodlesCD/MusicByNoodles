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
import com.csdurnan.music.dc.Artist
import com.csdurnan.music.ui.albums.AllAlbumsDirections
import com.csdurnan.music.ui.artists.AllArtistsDirections

class AllArtistsAdapter(private val artistList: ArrayList<Artist>, private val fragment: Fragment) : RecyclerView.Adapter<AllArtistsAdapter.ViewHolder>() {
    /**
     * Provides a reference to the type of views that we will be using.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val artistName: TextView
        val artistDetails: TextView
        val artistArtwork: ImageView
        val row: ConstraintLayout

        init {
            artistName = view.findViewById(R.id.tv_artist_name)
            artistDetails = view.findViewById(R.id.tv_artist_details)
            artistArtwork = view.findViewById(R.id.iv_artist_image)
            row = view.findViewById(R.id.cl_all_artist_list_row)
        }
    }

    /**
     * RecyclerView calls this method when it needs to create a new ViewHolder.
     * This method creates and initializes a ViewHolder and its associated view.
     * It does not fill in the view's contents with any specific data.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.artists_all_list_row, parent, false)
        return ViewHolder(itemView)
    }

    /**
     * RecyclerView calls this method to get a size of a dataset.
     * Uses this to determine when there are no more items that can be displayed.
     */
    override fun getItemCount(): Int {
        return artistList.size
    }

    /**
     * RecyclerView calls this method to associate a ViewHolder with data.
     * This method fetches appropriate data and uses it to fill in the layout.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.artistName.text = artistList[position].artistTitle
        holder.artistDetails.text = "${artistList[position].albums.size} albums - ${artistList[position].songCount} songs"

        holder.row.setOnClickListener {
            val action = AllArtistsDirections.actionArtistsToCurrentArtist(artistList[position])
            it.findNavController().navigate(action)
        }

        Glide.with(fragment)
            .load(artistList[position].albums[0].albumUri)
            .placeholder(R.drawable.image)
            .into(holder.artistArtwork)

    }
}