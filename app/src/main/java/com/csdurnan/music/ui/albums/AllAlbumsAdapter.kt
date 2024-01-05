package com.csdurnan.music.ui.albums

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
import com.csdurnan.music.dc.Album

class AllAlbumsAdapter(
    private val albumsList: List<Album>,
    private val fragment: Fragment,
    private val onAllAlbumsItemClickListener: OnAllAlbumsItemClickListener
) : RecyclerView.Adapter<AllAlbumsAdapter.ViewHolder>() {
    /**
     * Provides a reference to the type of views that we will be using.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val albumName: TextView
        val artistName: TextView
        val image: ImageView
        val row: ConstraintLayout
        val popupMenuButton: ImageButton
        val popupMenu: PopupMenu

        init {
            albumName = view.findViewById(R.id.tv_all_albums_row_title)
            artistName = view.findViewById(R.id.tv_all_albums_row_artist)
            image = view.findViewById(R.id.iv_all_albums_row_image)
            row = view.findViewById(R.id.cl_all_albums_row)
            popupMenuButton = view.findViewById(R.id.ib_albums_all_list_button)
            popupMenu = PopupMenu(view.context, popupMenuButton)
            popupMenu.inflate(R.menu.album_list_popup)

            popupMenuButton.setOnClickListener {
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
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.all_albums_row, parent, false)
        return ViewHolder(itemView)
    }

    /**
     * RecyclerView calls this method to get a size of a dataset.
     * Uses this to determine when there are no more items that can be displayed.
     */
    override fun getItemCount(): Int {
        return albumsList.size
    }

    /**
     * RecyclerView calls this method to associate a ViewHolder with data.
     * This method fetches appropriate data and uses it to fill in the layout.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.albumName.text = albumsList[position].title
        holder.artistName.text = albumsList[position].artist
        holder.row.setOnClickListener {
            val action = AllAlbumsDirections.actionAllAlbumsToCurrentAlbum(albumsList[position])
            it.findNavController().navigate(action)
        }

        holder.popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.song_list_popup_add_queue -> {
                    onAllAlbumsItemClickListener.onAllAlbumsItemClick(item.itemId, albumsList[position])
                    true
                }
                R.id.song_list_popup_add_playlist -> {
                    onAllAlbumsItemClickListener.onAllAlbumsItemClick(item.itemId, albumsList[position])
                    true
                }
                R.id.song_list_popup_artist -> {
                    onAllAlbumsItemClickListener.onAllAlbumsItemClick(item.itemId, albumsList[position])
                    true
                }
                else -> false
            }
        }

        Glide.with(fragment)
            .load(albumsList[position].albumUri)
            .placeholder(R.drawable.image)
            .error(R.drawable.image)
            .fallback(R.drawable.image)
            .into(holder.image)
    }

    interface OnAllAlbumsItemClickListener {
        fun onAllAlbumsItemClick(position: Int, album: Album)
    }
}