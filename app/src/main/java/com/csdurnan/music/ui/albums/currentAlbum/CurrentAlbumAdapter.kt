package com.csdurnan.music.ui.albums.currentAlbum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.csdurnan.music.R
import com.csdurnan.music.dc.Album
import com.csdurnan.music.dc.Song

class CurrentAlbumAdapter(private val currentAlbum: Album, private val onAlbumItemClickListener: OnAlbumItemClickListener, private val fragment: Fragment) : RecyclerView.Adapter<CurrentAlbumAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val currentRow: ConstraintLayout
        val index: TextView
        val songName: TextView

        val popupMenuButton: ImageButton
        val popupMenu: PopupMenu

        init {
            index = view.findViewById(R.id.tv_current_album_list_index)
            songName = view.findViewById(R.id.tv_current_album_list_title)
            currentRow = view.findViewById(R.id.cl_current_album_song_row)

            popupMenuButton = view.findViewById(R.id.ib_current_album_songs_button)
            popupMenu = PopupMenu(view.context, popupMenuButton)
            popupMenu.inflate(R.menu.popup_album_list)
            popupMenuButton.setOnClickListener { popupMenu.show() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_current_album, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return currentAlbum.songs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.index.text = (position + 1).toString()
        holder.songName.text = currentAlbum.songs[position].title
        holder.currentRow.setOnClickListener {
            val action =
                CurrentAlbumDirections.actionGlobalCurrentSong(currentAlbum.songs[position].id)
            it.findNavController().navigate(action)
        }
        holder.popupMenu.setOnMenuItemClickListener {item ->
            when (item.itemId) {
                R.id.song_list_popup_add_queue -> {
                    onAlbumItemClickListener.onAlbumItemClick(item.itemId, currentAlbum.songs[position])
                    true
                }
                R.id.song_list_pop_add_playlist -> {
                    onAlbumItemClickListener.onAlbumItemClick(item.itemId, currentAlbum.songs[position])
                    true
                }
                R.id.song_list_popup_artist -> {
                    onAlbumItemClickListener.onAlbumItemClick(item.itemId, currentAlbum.songs[position])
                    true
                }
                R.id.song_list_popup_album -> {
                    onAlbumItemClickListener.onAlbumItemClick(item.itemId, currentAlbum.songs[position])
                    true
                }
                else -> false
            }
        }
    }

    interface OnAlbumItemClickListener {
        fun onAlbumItemClick(position: Int, song: Song)
    }
}