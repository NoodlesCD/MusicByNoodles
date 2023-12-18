package com.csdurnan.music.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csdurnan.music.R

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
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_all_playlists, container, false)
        var allPlaylistsRecyclerView = view.findViewById<RecyclerView>(R.id.rv_all_playlists_list)
        allPlaylistsRecyclerView.layoutManager = LinearLayoutManager(view.context)
        allPlaylistsRecyclerView.setHasFixedSize(true)
        allPlaylistsRecyclerView.adapter = AllPlaylistsAdapter()
        return view
    }
}