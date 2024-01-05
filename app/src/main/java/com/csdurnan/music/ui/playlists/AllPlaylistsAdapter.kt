package com.csdurnan.music.ui.playlists

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
import com.csdurnan.music.dc.Playlist

class AllPlaylistsAdapter(
    private var playlistsList: List<Playlist>,
    private val fragment: Fragment
) : RecyclerView.Adapter<AllPlaylistsAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var row: ConstraintLayout
        var image: ImageView
        var title: TextView
        var info: TextView

        init {
            row = view.findViewById<ConstraintLayout>(R.id.cl_all_playlists_list_row)
            image = view.findViewById<ImageView>(R.id.iv_all_playlists_row_image)
            title = view.findViewById<TextView>(R.id.tv_playlists_all_list_title)
            info = view.findViewById<TextView>(R.id.tv_playlists_all_list_info)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.playlists_all_list_row, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return playlistsList.size
    }

    @SuppressLint("StringFormatMatches")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (playlistsList.isNotEmpty()) {
            holder.title.text = playlistsList[position].name
            holder.info.text =
                fragment.context?.getString(R.string.playlists_songs, playlistsList[position].songCount)

            Glide.with(fragment)
                .load(playlistsList[position].albumUri)
                .placeholder(R.drawable.image)
                .into(holder.image)

            holder.row.setOnClickListener {
                val action = AllPlaylistsDirections.actionAllPlaylistsToCurrentPlaylist(playlistsList[position].id!!, playlistsList[position])
                it.findNavController().navigate(action)
            }
        }
    }

    fun setList(playlistList: List<Playlist>) {
        this.playlistsList = playlistList
        this.notifyDataSetChanged()
    }
}
