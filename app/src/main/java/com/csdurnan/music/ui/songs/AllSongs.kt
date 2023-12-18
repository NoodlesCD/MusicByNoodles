package com.csdurnan.music.ui.songs

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csdurnan.music.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Contains a list of all the songs on the user's device.
 */
class AllSongs : Fragment() {

    /** Used for opening a dropdown menu for additional options related to each song */
    private lateinit var onSongsItemClickListener: AllSongsAdapter.OnSongsItemClickListener

    /** Pages songs from the ContentManagement class */
    private lateinit var allSongsPagingAdapter: AllSongsPagingAdapter

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_songs, container, false)

        /** Creates a ViewModel used to page the song list to the RecyclerView. */
        val viewModel by viewModels<AllSongsViewModel>()
        viewModel.setContentManagement(requireActivity().contentResolver)

        val allSongsRecyclerView = view.findViewById<RecyclerView>(R.id.rv_all_songs_list)
        allSongsPagingAdapter = AllSongsPagingAdapter(onSongsItemClickListener)
        allSongsRecyclerView.adapter = allSongsPagingAdapter
        allSongsRecyclerView.layoutManager = LinearLayoutManager(view.context)
        allSongsRecyclerView.setHasFixedSize(true)

        /** Pages data to the RecyclerView as the user scrolls. */
        lifecycleScope.launch {
            viewModel.getAllSongsPagingData().collectLatest { pagingData ->
                allSongsPagingAdapter.submitData(pagingData)
            }
        }

        return view
    }

    /** Sets the [onSongsItemClickListener] as implemented in the MainActivity. */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        onSongsItemClickListener = context as AllSongsAdapter.OnSongsItemClickListener
    }
}