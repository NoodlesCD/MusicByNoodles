package com.csdurnan.music.ui.artists

import android.content.ContentResolver
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csdurnan.music.utils.ContentManagement
import com.csdurnan.music.R

/**
 * A simple [Fragment] subclass.
 * Use the [AllArtists.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllArtists : Fragment() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val resolver: ContentResolver = requireActivity().contentResolver
        val cm: ContentManagement = ContentManagement(resolver)

        var view = inflater.inflate(R.layout.fragment_all_artists, container, false)
        var allArtistRecyclerView = view.findViewById<RecyclerView>(R.id.rv_all_artists_list)
        allArtistRecyclerView.layoutManager = LinearLayoutManager(view.context)
        allArtistRecyclerView.setHasFixedSize(true)
        allArtistRecyclerView.adapter = AllArtistsAdapter(cm.artists, this)

        return view
    }
}