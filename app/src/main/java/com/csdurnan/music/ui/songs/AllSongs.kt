package com.csdurnan.music.ui.songs

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csdurnan.music.utils.ContentManagement
import com.csdurnan.music.R
import com.csdurnan.music.dc.Playlist
import com.csdurnan.music.ui.playlists.NewPlaylistDirections
import com.csdurnan.music.utils.database.PlaylistDatabase
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 * Contains a list of all the songs on the user's device.
 */
class AllSongs : Fragment() {

    /** Used for opening a dropdown menu for additional options related to each song */
    private lateinit var onSongsItemClickListener: AllSongsAdapter.OnSongsItemClickListener


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_songs, container, false)
        val cm = ContentManagement(requireContext().contentResolver)

        val allSongsRecyclerView = view.findViewById<RecyclerView>(R.id.rv_all_songs_list)
        allSongsRecyclerView.adapter = AllSongsAdapter(cm.songs, this, onSongsItemClickListener)
        allSongsRecyclerView.layoutManager = LinearLayoutManager(view.context)
        allSongsRecyclerView.setHasFixedSize(true)

        return view
    }

    /** Sets the [onSongsItemClickListener] as implemented in the MainActivity. */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        onSongsItemClickListener = context as AllSongsAdapter.OnSongsItemClickListener
    }
}