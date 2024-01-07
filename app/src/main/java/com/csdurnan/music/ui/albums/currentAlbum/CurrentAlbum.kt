package com.csdurnan.music.ui.albums.currentAlbum

import android.content.Context
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
import com.csdurnan.music.R
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

/**
 * A simple [Fragment] subclass.
 * Created when a user wishes to view a specific album.
 */
@RequiresApi(Build.VERSION_CODES.Q)
class CurrentAlbum : Fragment() {

    private lateinit var onAlbumItemClickListener: CurrentAlbumAdapter.OnAlbumItemClickListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_current_album, container, false)
        val args: CurrentAlbumArgs by navArgs()

        view.findViewById<TextView>(R.id.tv_current_album_title).text = args.selectedAlbum.title
        view.findViewById<TextView>(R.id.tv_current_album_artist).text = args.selectedAlbum.artist

        val selectedAlbumImage = view?.findViewById<ImageView>(R.id.iv_current_album_image)
        if (selectedAlbumImage != null) {
            Glide.with(this)
                .load(args.selectedAlbum.songs[0].imageUri)
                .placeholder(R.drawable.artwork_placeholder)
                .into(selectedAlbumImage)
        }

        val currentAlbumRecyclerView = view.findViewById<RecyclerView>(R.id.rv_current_album_recycler)
        currentAlbumRecyclerView.setHasFixedSize(true)
        currentAlbumRecyclerView.layoutManager = LinearLayoutManager(view.context)
        currentAlbumRecyclerView.adapter = parentFragment?.context?.let {
            CurrentAlbumAdapter(args.selectedAlbum, onAlbumItemClickListener, this)
        }

        val decorView = activity?.window?.decorView
        val rootView = decorView?.findViewById(R.id.nav_host_fragment) as ViewGroup
        val windowBackground = decorView.background

        val blurView = view.findViewById<BlurView>(R.id.bv_current_album)
        blurView.setupWith(rootView, RenderScriptBlur(requireContext()))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(3f)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onAlbumItemClickListener = context as CurrentAlbumAdapter.OnAlbumItemClickListener
    }
}