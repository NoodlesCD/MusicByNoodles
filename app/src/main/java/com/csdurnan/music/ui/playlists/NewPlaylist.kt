package com.csdurnan.music.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csdurnan.music.R
import com.csdurnan.music.dc.Playlist
import com.csdurnan.music.utils.database.PlaylistDatabase

/**
 * A simple [Fragment] subclass.
 * Use the [NewPlaylist.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewPlaylist : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_playlist, container, false)
        val args: NewPlaylistArgs by navArgs()

        val db = PlaylistDatabase.getInstance(requireContext()).dao
        val playlists: List<Playlist> = listOf()

        val playlistsRecyclerView = view.findViewById<RecyclerView>(R.id.rv_new_playlist_list)
        val adapter = NewPlaylistAdapter(args.song.asList(), playlists, this)

        playlistsRecyclerView.setHasFixedSize(true)
        playlistsRecyclerView.layoutManager = LinearLayoutManager(view.context)
        playlistsRecyclerView.adapter = adapter

        db.getPlaylists().observe(viewLifecycleOwner) { playlistData ->
            adapter.setList(playlistData)
        }

        return view
    }

}