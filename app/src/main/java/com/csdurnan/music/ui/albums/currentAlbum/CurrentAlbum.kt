package com.csdurnan.music.ui.albums.currentAlbum

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Size
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
 * Created when a user views an Album.
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
                .placeholder(R.drawable.image)
                .into(selectedAlbumImage)
        }

        val currentAlbumRecyclerView = view.findViewById<RecyclerView>(R.id.rv_current_album_recycler)
        currentAlbumRecyclerView.setHasFixedSize(true)
        currentAlbumRecyclerView.layoutManager = LinearLayoutManager(view.context)
        currentAlbumRecyclerView.adapter = parentFragment?.context?.let {
            CurrentAlbumAdapter(args.selectedAlbum, onAlbumItemClickListener, this)
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onAlbumItemClickListener = context as CurrentAlbumAdapter.OnAlbumItemClickListener
    }
}