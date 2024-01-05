package com.csdurnan.music.ui.playlists.currentPlaylist

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.csdurnan.music.R
import com.csdurnan.music.dc.Playlist
import com.csdurnan.music.dc.PlaylistSongCrossRef
import com.csdurnan.music.dc.Song
import com.csdurnan.music.utils.PlaylistDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CurrentPlaylistAdapter(
    var songs: List<Song>,
    val fragment: CurrentPlaylist
) : RecyclerView.Adapter<CurrentPlaylistAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var currentRow: ConstraintLayout
        var songImage: ImageView
        var songTitle: TextView
        var songArtist: TextView
        var imageButton: ImageButton
        var popupMenu: PopupMenu

        init {
            currentRow = view.findViewById(R.id.cl_current_playlist_list_row)
            songImage = view.findViewById(R.id.iv_current_playlist_row_image)
            songTitle = view.findViewById(R.id.tv_current_playlist_list_title)
            songArtist = view.findViewById(R.id.tv_current_playlist_list_artist)
            imageButton = view.findViewById(R.id.ib_current_playlist_list_button)
            popupMenu = PopupMenu(view.context, imageButton)
            popupMenu.inflate(R.menu.remove_song)
            imageButton.setOnClickListener {
                popupMenu.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.current_playlist_row, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return songs.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.songTitle.text = songs[position].title
        holder.songArtist.text = songs[position].artist
        Glide.with(fragment)
            .load(songs[position].imageUri)
            .placeholder(R.drawable.image)
            .into(holder.songImage)

        holder.currentRow.setOnClickListener {
            val action = CurrentPlaylistDirections.actionGlobalCurrentSong(songs[position].id)
            it.findNavController().navigate(action)
        }

        holder.popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.remove_song -> {
                    val builder = AlertDialog.Builder(fragment.context)
                    builder
                        .setTitle("Remove song?")
                        .setPositiveButton("Confirm") {
                                _, _ ->
                            GlobalScope.launch {
                                fragment.deletePlaylistSong(songs[position].id)
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

    fun setList(songs: List<Song>) {
        this.songs = songs
        this.notifyDataSetChanged()
    }

    interface DeletePlaylistSong {
        suspend fun deletePlaylistSong(songId: Long)
    }


}
