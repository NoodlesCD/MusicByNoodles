package com.csdurnan.music.ui.playlists

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.csdurnan.music.R
import com.csdurnan.music.dc.Playlist
import com.csdurnan.music.dc.Song
import com.csdurnan.music.utils.PlaylistDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.ArrayList

class NewPlaylistAdapter(
    private val songToAdd: Song,
    private var playlists: List<Playlist>,
    private val parentFragment: Fragment
) : RecyclerView.Adapter<NewPlaylistAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var currentRow: ConstraintLayout
        var playlistImage: ImageView
        var playlistTitle: TextView
        var playlistSongCount: TextView

        init {
            currentRow = view.findViewById<ConstraintLayout>(R.id.cl_all_playlists_list_row)
            playlistImage = view.findViewById<ImageView>(R.id.iv_all_playlists_row_image)
            playlistTitle = view.findViewById<TextView>(R.id.tv_playlists_all_list_title)
            playlistSongCount = view.findViewById<TextView>(R.id.tv_playlists_all_list_info)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.playlists_all_list_row, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        if (playlists.isEmpty()) return 1
        return playlists.size + 1
    }

    @SuppressLint("StringFormatMatches")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            holder.playlistTitle.text = parentFragment.context?.getString(R.string.new_playlist)
            holder.playlistSongCount.text = ""
            Glide.with(parentFragment)
                .load(R.drawable.image)
                .into(holder.playlistImage)

            val editText = EditText(parentFragment.requireContext())
            val builder = AlertDialog.Builder(parentFragment.context)
            builder
                .setTitle("New playlist")
                .setView(editText)
                .setPositiveButton("Confirm") {
                        _, _ ->
                    val db = PlaylistDatabase.getInstance(parentFragment.requireContext())
                    val pLDao = db.dao
                    val playlist = Playlist(
                        null,
                        editText.text.toString(),
                        1,
                        songToAdd.imageUri,
                        listOf(songToAdd))
                    GlobalScope.launch {
                        pLDao.insertPlaylistAndSongs(playlist)
                    }
                    val action = NewPlaylistDirections.actionNewPlaylistToAllPlaylists()
                    holder.currentRow.findNavController().navigate(action)
                }
                .setNegativeButton("Cancel") {
                        dialog, _ ->
                    dialog.cancel()
                }

            holder.currentRow.setOnClickListener {
                val dialog = builder.create()
                dialog.show()
            }

        } else if (position > 0) {
            val arrayPosition = position - 1
            holder.playlistTitle.text = playlists[arrayPosition].name
            holder.playlistSongCount.text =
                parentFragment.context?.getString(
                    R.string.playlists_songs,
                    playlists[arrayPosition].songCount)

            val image = playlists[arrayPosition].albumUri
            Glide.with(parentFragment)
                .load(image)
                .placeholder(R.drawable.image)
                .into(holder.playlistImage)

            holder.currentRow.setOnClickListener {
                GlobalScope.launch {
                    val db = PlaylistDatabase.getInstance(parentFragment.requireContext()).dao
                    db.insertSongToPlaylist(playlists[arrayPosition].id!!, songToAdd)
                }

                val action = NewPlaylistDirections.actionNewPlaylistToAllPlaylists()
                it.findNavController().navigate(action)
            }
        }
    }

    fun setList(playlistList: List<Playlist>) {
        this.playlists = playlistList
        this.notifyDataSetChanged()
    }
}
