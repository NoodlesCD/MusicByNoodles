package com.csdurnan.music.ui.albums

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
import com.csdurnan.music.ContentManagement
import com.csdurnan.music.R
import com.csdurnan.music.adapters.AllAlbumsAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [AllAlbums.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllAlbums : Fragment() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val resolver: ContentResolver = requireActivity().contentResolver
        val cm: ContentManagement = ContentManagement(resolver)

        var view = inflater.inflate(R.layout.fragment_all_albums, container, false)
        var allAlbumsRecyclerView = view.findViewById<RecyclerView>(R.id.rv_all_albums_list)
        allAlbumsRecyclerView.layoutManager = LinearLayoutManager(view.context)
        allAlbumsRecyclerView.setHasFixedSize(true)
        allAlbumsRecyclerView.adapter = AllAlbumsAdapter(cm.albums, this)

        return view
    }
}