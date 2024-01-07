package com.csdurnan.music.ui.albums

import android.content.Context
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
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

/**
 * A simple [Fragment] subclass.
 * Use the [AllAlbums.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllAlbums : Fragment() {

    private lateinit var onAllAlbumsItemClickListener: AllAlbumsAdapter.OnAllAlbumsItemClickListener

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val cm = ContentManagement(requireActivity().contentResolver)

        val view = inflater.inflate(R.layout.fragment_all_albums, container, false)
        val allAlbumsRecyclerView = view.findViewById<RecyclerView>(R.id.rv_all_albums_list)
        allAlbumsRecyclerView.setHasFixedSize(true)
        allAlbumsRecyclerView.layoutManager = LinearLayoutManager(view.context)
        allAlbumsRecyclerView.adapter = AllAlbumsAdapter(cm.albums, this, onAllAlbumsItemClickListener)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onAllAlbumsItemClickListener = context as AllAlbumsAdapter.OnAllAlbumsItemClickListener
    }
}