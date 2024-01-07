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
import com.csdurnan.music.utils.ContentManagement
import com.csdurnan.music.R
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

/**
 * A simple [Fragment] subclass.
 * Use the [current_artist.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrentArtist : Fragment() {
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
            .placeholder(R.drawable.artwork_placeholder)
            .into(image)

        var currentArtistAlbumRecycler = view.findViewById<RecyclerView>(R.id.rv_current_artist_recycler)
        currentArtistAlbumRecycler.layoutManager = LinearLayoutManager(view.context)
        currentArtistAlbumRecycler.setHasFixedSize(true)
        currentArtistAlbumRecycler.adapter = parentFragment?.context?.let { CurrentArtistAdapter(args.selectedArtist.albums, this) }

        val decorView = activity?.window?.decorView
        val rootView = decorView?.findViewById(R.id.nav_host_fragment) as ViewGroup
        val windowBackground = decorView.background

        val blurView = view.findViewById<BlurView>(R.id.bv_current_artist)
        blurView.setupWith(rootView, RenderScriptBlur(requireContext()))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(3f)

        return view
    }
}