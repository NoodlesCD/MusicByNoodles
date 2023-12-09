package com.csdurnan.music.ui.songs

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.csdurnan.music.R
import com.csdurnan.music.adapters.AllSongsAdapter
import com.csdurnan.music.dc.Song

class AllSongsViewHolder(val view: View, private val onSongsItemClickListener: AllSongsAdapter.OnSongsItemClickListener) : RecyclerView.ViewHolder(view) {
    private val songName: TextView = view.findViewById(R.id.tv_songs_all_list_title)
    private val artistName: TextView = view.findViewById(R.id.tv_songs_all_list_artist)
    val image: ImageView = view.findViewById(R.id.iv_all_songs_row_image)
    private val row: ConstraintLayout = view.findViewById(R.id.cl_all_songs_list_row)
    private val popupMenuButton: ImageButton = view.findViewById(R.id.ib_songs_all_list_button)
    private val popupMenu: PopupMenu = PopupMenu(view.context, popupMenuButton)

    init {
        popupMenu.inflate(R.menu.song_list_popup)
        popupMenuButton.setOnClickListener {
            popupMenu.show()
        }
    }

    fun bind(song: Song) {
        songName.text = song.title
        artistName.text = song.artist

        row.setOnClickListener {
            val action = AllSongsDirections.actionGlobalCurrentSong(song.id)
            it.findNavController().navigate(action)
        }

        popupMenu.setOnMenuItemClickListener {item ->
            when (item.itemId) {
                R.id.song_list_popup_add_queue -> {
                    onSongsItemClickListener.onSongItemClick(item.itemId, song)
                    true
                }
                R.id.song_list_popup_artist -> {
                    onSongsItemClickListener.onSongItemClick(item.itemId, song)
                    true
                }
                R.id.song_list_popup_album -> {
                    onSongsItemClickListener.onSongItemClick(item.itemId, song)
                    true
                }
                else -> false
            }
        }

        Glide.with(view)
            .load(song.imageUri)
            .placeholder(R.drawable.image)
            .into(image)
    }
}