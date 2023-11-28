package com.csdurnan.music.fragments

import android.content.ContentResolver
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csdurnan.music.ContentManagement
import com.csdurnan.music.R
import com.csdurnan.music.adapters.AllAlbumsAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AllAlbums.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllAlbums : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AlbumsAll.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AllAlbums().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}