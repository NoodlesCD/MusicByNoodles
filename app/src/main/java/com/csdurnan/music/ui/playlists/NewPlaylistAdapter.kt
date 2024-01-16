package com.csdurnan.music.ui.playlists

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.csdurnan.music.R
import com.csdurnan.music.dc.Playlist
import com.csdurnan.music.dc.Song
import com.csdurnan.music.utils.database.PlaylistDatabase
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class NewPlaylistAdapter(
    private val songsToAdd: List<Song>,
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
            .inflate(R.layout.row_all_playlists, parent, false)
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
                .load(R.drawable.artwork_placeholder)
                .into(holder.playlistImage)

            holder.currentRow.setOnClickListener {
                val builder =
                    AlertDialog.Builder(parentFragment.requireContext(), R.style.WrapContentDialog)
                val dialogView = parentFragment.activity?.layoutInflater?.inflate(
                    R.layout.dialog_new_playlist,
                    null
                )
                val dialog = builder.setView(dialogView).create()
                dialogView?.findViewById<TextView>(R.id.tv_new_playlist_dismiss)
                    ?.setOnClickListener {
                        dialog.dismiss()
                    }
                dialogView?.findViewById<TextView>(R.id.tv_new_playlist_confirm)
                    ?.setOnClickListener {
                        val playlistInput = dialogView.findViewById<EditText>(R.id.et_new_playlist)

                        val db = PlaylistDatabase.getInstance(parentFragment.requireContext())
                        val pLDao = db.dao
                        val playlist = Playlist(
                            null,
                            playlistInput.text.toString(),
                            songsToAdd.size,
                            songsToAdd[0].imageUri,
                            songsToAdd
                        )
                        GlobalScope.launch {
                            pLDao.insertPlaylistAndSongs(playlist)
                        }
                        val action = NewPlaylistDirections.actionNewPlaylistToAllPlaylists()
                        holder.currentRow.findNavController().navigate(action)

                        dialog.dismiss()
                    }

                val decorView = parentFragment.activity?.window?.decorView
                val rootView = decorView?.findViewById(R.id.nav_host_fragment) as ViewGroup
                val windowBackground = decorView.background

                val dialogBlurView = dialogView?.findViewById<BlurView>(R.id.dialog_new_playlist)
                dialogBlurView?.setupWith(
                    rootView,
                    RenderScriptBlur(parentFragment.requireContext())
                )
                    ?.setFrameClearDrawable(windowBackground)
                    ?.setBlurRadius(3f)

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
                .placeholder(R.drawable.artwork_placeholder)
                .into(holder.playlistImage)

            holder.currentRow.setOnClickListener {
                GlobalScope.launch {
                    val db = PlaylistDatabase.getInstance(parentFragment.requireContext()).dao
                    for (song in songsToAdd) {
                        db.insertSongToPlaylist(playlists[arrayPosition].id!!, song)
                    }
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
