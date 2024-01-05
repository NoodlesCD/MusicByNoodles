package com.csdurnan.music.ui.artists

import android.annotation.SuppressLint
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

class AllArtistsAdapter(private val artistList: List<Artist>, private val fragment: Fragment) : RecyclerView.Adapter<AllArtistsAdapter.ViewHolder>() {
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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_all_artists, parent, false)
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
    @SuppressLint("StringFormatMatches")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.artistName.text = artistList[position].name
        holder.artistDetails.text = fragment.context?.getString(
            R.string.albums_songs,
            artistList[position].albums.size,
            artistList[position].songCount
        )

        holder.row.setOnClickListener {
            val action = AllArtistsDirections.actionArtistsToCurrentArtist(artistList[position])
            it.findNavController().navigate(action)
        }

        Glide.with(fragment)
            .load(artistList[position].albums[0].albumUri)
            .placeholder(R.drawable.artwork_placeholder)
            .error(R.drawable.artwork_placeholder)
            .fallback(R.drawable.artwork_placeholder)
            .into(holder.artistArtwork)
    }
}