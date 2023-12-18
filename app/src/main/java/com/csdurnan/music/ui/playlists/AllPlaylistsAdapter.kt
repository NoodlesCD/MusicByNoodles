package com.csdurnan.music.ui.playlists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.csdurnan.music.R
import com.csdurnan.music.dc.Playlist

class AllPlaylistsAdapter(
    private val playlistsList: ArrayList<Playlist>,
    private val fragment: Fragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val row = view.findViewById<ConstraintLayout>(R.id.cl_all_playlists_list_row)
        val image = view.findViewById<ImageView>(R.id.iv_all_playlists_row_image)
        val title = view.findViewById<TextView>(R.id.tv_playlists_all_list_title)
        val info = view.findViewById<TextView>(R.id.tv_playlists_all_list_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.playlists_all_list_row, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return playlistsList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }
}
