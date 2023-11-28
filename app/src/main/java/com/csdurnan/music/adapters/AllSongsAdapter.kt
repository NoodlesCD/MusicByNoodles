package com.csdurnan.music.adapters

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.csdurnan.music.R
import com.csdurnan.music.dc.Song
import com.csdurnan.music.fragments.AllArtistsDirections
import com.csdurnan.music.fragments.AllSongsDirections
import com.csdurnan.music.fragments.CurrentSong
import java.io.FileNotFoundException

class AllSongsAdapter(private val songList: List<Song>, private val fragment: Fragment, private val onSongsItemClickListener: OnSongsItemClickListener) : RecyclerView.Adapter<AllSongsAdapter.ViewHolder>() {
    /**
     * Provides a reference to the type of views that we will be using.
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songName: TextView
        val artistName: TextView
        val image: ImageView
        val row: ConstraintLayout
        val popupMenuButton: ImageButton
        val popupMenu: PopupMenu

        init {
            songName = view.findViewById(R.id.tv_songs_all_list_title)
            artistName = view.findViewById(R.id.tv_songs_all_list_artist)
            image = view.findViewById(R.id.iv_all_songs_row_image)
            row = view.findViewById(R.id.cl_all_songs_list_row)
            popupMenuButton = view.findViewById(R.id.ib_songs_all_list_button)
            popupMenu = PopupMenu(view.context, view)
            popupMenu.inflate(R.menu.song_list_popup)

            popupMenuButton.setOnClickListener {
                popupMenu.gravity = Gravity.END
                popupMenu.show()
            }
        }
    }

    /**
     * RecyclerView calls this method when it needs to create a new ViewHolder.
     * This method creates and initializes a ViewHolder and its associated view.
     * It does not fill in the view's contents with any specific data.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.songs_all_list_row, parent, false)
        return ViewHolder(view)
    }

    /**
     * RecyclerView calls this method to get a size of a dataset.
     * Uses this to determine when there are no more items that can be displayed.
     */
    override fun getItemCount(): Int {
        return songList.size
    }

    /**
     * RecyclerView calls this method to associate a ViewHolder with data.
     * This method fetches appropriate data and uses it to fill in the layout.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.songName.text = songList[position].title
        holder.artistName.text = songList[position].artist

        holder.row.setOnClickListener {
            val action = AllSongsDirections.actionGlobalCurrentSong(songList[position].id)
            it.findNavController().navigate(action)
        }

        holder.popupMenu.setOnMenuItemClickListener {item ->
            when (item.itemId) {
                R.id.song_list_popup_add_queue -> {
                    onSongsItemClickListener.onItemClick(item.itemId, songList[position])
                    true
                }
                R.id.song_list_popup_artist -> {
                    onSongsItemClickListener.onItemClick(item.itemId, songList[position])
                    true
                }
                R.id.song_list_popup_album -> {
                    onSongsItemClickListener.onItemClick(item.itemId, songList[position])
                    true
                }
                else -> false
            }
        }

        Glide.with(fragment)
            .load(songList[position].imageUri)
            .placeholder(R.drawable.image)
            .into(holder.image)
    }

    interface OnSongsItemClickListener {
        fun onItemClick(position: Int, song: Song)
    }
}