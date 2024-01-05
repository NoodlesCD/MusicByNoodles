package com.csdurnan.music.ui.artists.currentArtist

import android.content.ContentResolver
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.csdurnan.music.ContentManagement
import com.csdurnan.music.R

/**
 * A simple [Fragment] subclass.
 * Use the [current_artist.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrentArtist : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val resolver: ContentResolver = requireActivity().contentResolver
        val cm: ContentManagement = ContentManagement(resolver)

        var view = inflater.inflate(R.layout.fragment_current_artist, container, false)

        val args: CurrentArtistArgs by navArgs()

        view.findViewById<TextView>(R.id.tv_current_artist_title).text = args.selectedArtist.name
        view.findViewById<TextView>(R.id.tv_current_artist_info).text = "" + args.selectedArtist.albums.size + " albums - " + args.selectedArtist.songCount + " songs"

        val image = view.findViewById<ImageView>(R.id.iv_current_artist_image)

        Glide.with(this)
            .load(args.selectedArtist.albums[0].albumUri)
            .placeholder(R.drawable.image)
            .into(image)

        var currentArtistAlbumRecycler = view.findViewById<RecyclerView>(R.id.rv_current_artist_recycler)
        currentArtistAlbumRecycler.layoutManager = LinearLayoutManager(view.context)
        currentArtistAlbumRecycler.setHasFixedSize(true)
        currentArtistAlbumRecycler.adapter = parentFragment?.context?.let { CurrentArtistAdapter(args.selectedArtist.albums, this) }



        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CurrentAlbum.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CurrentArtist().apply {

            }
    }
}