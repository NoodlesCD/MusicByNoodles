package com.csdurnan.music.fragments

import android.content.ContentResolver
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
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.csdurnan.music.ContentManagement
import com.csdurnan.music.R
import com.csdurnan.music.adapters.CurrentAlbumAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [CurrentAlbum.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrentAlbum : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var resolver: ContentResolver = requireActivity().contentResolver
        val cm: ContentManagement = ContentManagement(resolver)
        var view = inflater.inflate(R.layout.fragment_current_album, container, false)
        val args: CurrentAlbumArgs by navArgs()

        view.findViewById<TextView>(R.id.tv_current_album_title).text = args.selectedAlbum.albumTitle
        view.findViewById<TextView>(R.id.tv_current_album_artist).text = args.selectedAlbum.artist


        if (args.selectedAlbum.songs[0].uri != null) {
            val trackUri = args.selectedAlbum.songs[0].uri
            val cr = context?.contentResolver

            var bm: Bitmap? = null
            if (cr != null) {
                bm = trackUri?.let { cr.loadThumbnail(it, Size(1024, 1024), null) }
            }

            view?.findViewById<ImageView>(R.id.iv_current_album_image)?.setImageBitmap(bm)
            view?.findViewById<ImageView>(R.id.iv_current_album_image)?.scaleType =
                ImageView.ScaleType.FIT_CENTER
        }

        var currentAlbumRecyclerView = view.findViewById<RecyclerView>(R.id.rv_current_album_recycler)
        currentAlbumRecyclerView.layoutManager = LinearLayoutManager(view.context)
        currentAlbumRecyclerView.setHasFixedSize(true)
        currentAlbumRecyclerView.adapter = parentFragment?.context?.let { CurrentAlbumAdapter(args.selectedAlbum, this) }


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
            CurrentAlbum().apply {

            }
    }
}