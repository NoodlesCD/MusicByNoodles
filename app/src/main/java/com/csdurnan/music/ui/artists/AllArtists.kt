package com.csdurnan.music.ui.artists

import android.content.ContentResolver
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
import com.csdurnan.music.ui.songs.AllSongsAdapter
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.reddit.indicatorfastscroll.FastScrollerView
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

/**
 * A simple [Fragment] subclass.
 * Use the [AllArtists.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllArtists : Fragment() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val resolver: ContentResolver = requireActivity().contentResolver
        val cm: ContentManagement = ContentManagement(resolver)

        var view = inflater.inflate(R.layout.fragment_all_artists, container, false)

        var allArtistRecyclerView = view.findViewById<RecyclerView>(R.id.rv_all_artists_list)
        var allArtistRecyclerLayoutManager = LinearLayoutManager(view.context)
        allArtistRecyclerView.adapter = AllArtistsAdapter(cm.artists, this)
        allArtistRecyclerView.layoutManager = allArtistRecyclerLayoutManager
        allArtistRecyclerView.setHasFixedSize(true)

        val fastScrollerView = view.findViewById<FastScrollerView>(R.id.fs_all_artists)
        fastScrollerView.setupWithRecyclerView(
            allArtistRecyclerView,
            { position ->
                val item = cm.artists[position] // Get your model object
                // or fetch the section at [position] from your database
                FastScrollItemIndicator.Text(
                    item.name.substring(0, 1).uppercase() // Grab the first letter and capitalize it
                ) // Return a text indicator
            },
            showIndicator = { indicator, indicatorPosition, totalIndicators ->
                val text: FastScrollItemIndicator.Text = indicator as FastScrollItemIndicator.Text

                "0ABCDEFGHIJKLMNOPQRSTUVWXYZ".contains(text.text)
            }
        )
        fastScrollerView.useDefaultScroller = false
        fastScrollerView.itemIndicatorSelectedCallbacks += object : FastScrollerView.ItemIndicatorSelectedCallback {
            override fun onItemIndicatorSelected(
                indicator: FastScrollItemIndicator,
                indicatorCenterY: Int,
                itemPosition: Int
            ) {
                allArtistRecyclerView.stopScroll()
                allArtistRecyclerLayoutManager.scrollToPosition(itemPosition)
            }
        }

        return view
    }
}