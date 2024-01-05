package com.csdurnan.music.ui.playlists.currentPlaylist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.csdurnan.music.R
import com.csdurnan.music.dc.PlaylistSongCrossRef
import com.csdurnan.music.dc.Song
import com.csdurnan.music.utils.database.PlaylistDatabase

/**
 * A simple [Fragment] subclass.
 * Use the [CurrentPlaylist.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrentPlaylist : Fragment(), CurrentPlaylistAdapter.DeletePlaylistSong {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_current_playlist, container, false)

        val songsList: List<Song> = listOf()
        val args: CurrentPlaylistArgs by navArgs()
        val db = PlaylistDatabase.getInstance(requireContext()).dao

        val currentPlaylistRecyclerView =
            view.findViewById<RecyclerView>(R.id.rv_current_playlist_recycler)
        val adapter = CurrentPlaylistAdapter(songsList, this)
        currentPlaylistRecyclerView.layoutManager = LinearLayoutManager(view.context)
        currentPlaylistRecyclerView.setHasFixedSize(true)
        currentPlaylistRecyclerView.adapter = adapter

        val title = view.findViewById<TextView>(R.id.tv_current_playlist_title)
        title.text = args.playlist.name

        val songCount = view.findViewById<TextView>(R.id.tv_current_playlist_info)
        songCount.text = "${args.playlist.songCount} songs"

        val image = view.findViewById<ImageView>(R.id.iv_current_playlist_image)
        Glide.with(this)
            .load(args.playlist.albumUri)
            .placeholder(R.drawable.artwork_placeholder)
            .into(image)

        db.getSongsFromPlaylist(args.id).observe(viewLifecycleOwner) { playlistData ->
            adapter.setList(playlistData.songs)
        }

        return view
    }

    override suspend fun deletePlaylistSong(songId: Long) {
        val args: CurrentPlaylistArgs by navArgs()
        val db = PlaylistDatabase.getInstance(requireContext()).dao
        db.deleteSongFromPlaylist(PlaylistSongCrossRef(args.id, songId))
    }
}