package com.csdurnan.music.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csdurnan.music.R
import com.csdurnan.music.dc.Playlist
import com.csdurnan.music.utils.PlaylistDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [all_playlists.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllPlaylists : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_all_playlists, container, false)

        val playlists: List<Playlist> = listOf()
        val db = PlaylistDatabase.getInstance(requireContext()).dao

        val allPlaylistsRecyclerView = view.findViewById<RecyclerView>(R.id.rv_all_playlists_list)
        val adapter = AllPlaylistsAdapter(playlists, this)
        allPlaylistsRecyclerView.layoutManager = LinearLayoutManager(view.context)
        allPlaylistsRecyclerView.setHasFixedSize(true)
        allPlaylistsRecyclerView.adapter = adapter

        db.getPlaylists().observe(viewLifecycleOwner) { playlistData ->
            adapter.setList(playlistData)
        }

        return view
    }
}