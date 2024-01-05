package com.csdurnan.music.ui.playlists

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.csdurnan.music.R
import com.csdurnan.music.dc.Playlist
import com.csdurnan.music.utils.PlaylistDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AllPlaylistsAdapter(
    private var playlistsList: List<Playlist>,
    private val fragment: AllPlaylists
) : RecyclerView.Adapter<AllPlaylistsAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var row: ConstraintLayout
        var image: ImageView
        var title: TextView
        var info: TextView
        val popupMenuButton: ImageButton
        val popupMenu: PopupMenu

        init {
            row = view.findViewById<ConstraintLayout>(R.id.cl_all_playlists_list_row)
            image = view.findViewById<ImageView>(R.id.iv_all_playlists_row_image)
            title = view.findViewById<TextView>(R.id.tv_playlists_all_list_title)
            info = view.findViewById<TextView>(R.id.tv_playlists_all_list_info)
            popupMenuButton = view.findViewById(R.id.ib_playlists_all_list_button)
            popupMenu = PopupMenu(view.context, popupMenuButton)
            popupMenu.inflate(R.menu.delete_playlist)

            popupMenuButton.setOnClickListener {
                popupMenu.show()
            }
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

            holder.popupMenu.setOnMenuItemClickListener {  item ->
                when (item.itemId) {
                    R.id.delete_playlist -> {
                        val builder = AlertDialog.Builder(fragment.context)
                        builder
                            .setTitle("Delete playlist?")
                            .setPositiveButton("Confirm") {
                                    _, _ ->
                                GlobalScope.launch {
                                    fragment.deletePlaylist(playlistsList[position])
                                }
                            }
                            .setNegativeButton("Cancel") {
                                    dialog, _ ->
                                dialog.cancel()
                            }

                        val dialog = builder.create()
                        dialog.show()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    fun setList(playlistList: List<Playlist>) {
        this.playlistsList = playlistList
        this.notifyDataSetChanged()
    }

    interface DeletePlaylist {
        suspend fun deletePlaylist(playlist: Playlist)
    }
}
