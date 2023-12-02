package com.csdurnan.music.fragments

import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csdurnan.music.ContentManagement
import com.csdurnan.music.R
import com.csdurnan.music.adapters.AllSongsAdapter
import com.csdurnan.music.dc.Song
import com.csdurnan.music.utils.AllSongsPagingAdapter
import com.csdurnan.music.utils.AllSongsPagingSource
import com.csdurnan.music.utils.AllSongsViewModel
import com.csdurnan.music.utils.SongsListComparator
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Contains a list of all the songs on the user's device.
 */
class AllSongs : Fragment() {

    private lateinit var onSongsItemClickListener: AllSongsAdapter.OnSongsItemClickListener


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val resolver: ContentResolver = requireActivity().contentResolver
        val cm = ContentManagement(resolver)

        var view = inflater.inflate(R.layout.fragment_all_songs, container, false)


        val viewModel by viewModels<AllSongsViewModel>()
        val pagingAdapter = AllSongsPagingAdapter(SongsListComparator, onSongsItemClickListener)
        var allSongsRecyclerView = view.findViewById<RecyclerView>(R.id.rv_all_songs_list)
        allSongsRecyclerView.adapter = pagingAdapter

        lifecycleScope.launch {
            viewModel.data.observe(viewLifecycleOwner, Observer { ne})
        }





        //allSongsRecyclerView.adapter = pagingAdapter



        allSongsRecyclerView.layoutManager = LinearLayoutManager(view.context)
        allSongsRecyclerView.setHasFixedSize(true)
        allSongsRecyclerView.adapter = parentFragment?.context?.let { AllSongsAdapter(cm.songsSorted, this, onSongsItemClickListener) }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AllSongsAdapter.OnSongsItemClickListener) {
            onSongsItemClickListener = context
        } else {
            throw ClassCastException()
        }
    }
}