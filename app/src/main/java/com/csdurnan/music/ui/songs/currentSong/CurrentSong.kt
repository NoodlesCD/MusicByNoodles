package com.csdurnan.music.ui.songs.currentSong

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.csdurnan.music.R
import com.csdurnan.music.ui.songs.currentSong.CurrentSongArgs
import com.csdurnan.music.utils.MusicBinder
import com.csdurnan.music.utils.MusicService
import com.csdurnan.music.utils.UpdateUiBroadcastReceiver

/**
 * A simple [Fragment] subclass.
 * Created when a song is playing and contains UI controls over the song.
 */
class CurrentSong : Fragment(), CurrentSongChangeCallback {

    /**
     * Binds the CurrentSong fragment to the MusicService.
     * Allows interaction with the methods in the service.
     */
    private lateinit var serviceConnection: ServiceConnection
    private lateinit var musicService: MusicService
    private var serviceBound = false
    private val handler = Handler()

    private fun bindMusicService() {
        val serviceIntent = Intent(requireContext(), MusicService::class.java)
        serviceConnection = object : ServiceConnection {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as MusicBinder
                musicService = binder.getService
                serviceBound = true

                handler.post(currentPositionTimer)
                updateUi()
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                serviceBound = false
            }
        }

        requireContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    /** Updates the UI every second with the current position of the song */
    private val currentPositionTimer = object : Runnable {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun run() {
            activity?.runOnUiThread {
                if (serviceBound) {
                    var bar = view?.findViewById<SeekBar>(R.id.sbSongPositionBar)
                    bar?.progress = musicService.currentPosition()

                    val currentTime = view?.findViewById<TextView>(R.id.tvSongCurrentTime)
                    currentTime?.text = timeLabel(musicService.currentPosition())
                }
            }

            handler.postDelayed(this, 1000)
        }
    }

    /**
     * Implements the function within [CurrentSongChangeCallback].
     * Updates the UI whenever a song changes with the new song information.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onAction() {
        updateUi()
    }

    /** Communicates with the MainActivity. Used for setting the song to play. */
    private val viewModel: SongSelectorViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_current_song, container, false)

        /** The songId of the selected song will be passed when the fragment is created. */
        val args: CurrentSongArgs by navArgs()
        val songId = args.songId

        /** Start the musicService and setup a BroadcastReceiver to communicate UI changes. */
        bindMusicService()
        val broadcastReceiver = UpdateUiBroadcastReceiver(this, null)
        context?.registerReceiver(broadcastReceiver, IntentFilter("UPDATE_UI"))

        /** Clicking on the CurrentSong bar on the main menus will pass a value of 0L. */
        if (songId != 0L) {
            viewModel.selectSong(songId.toInt())
        }

        /** Play/Pause button functionality */
        val pauseButton = view.findViewById<ImageView>(R.id.ivPlayButton)
        pauseButton.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.baseline_pause_24,
                null
            )
        )
        pauseButton.setOnClickListener {
            if (musicService.isPlaying()) {
                musicService.pauseSong()
                pauseButton.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.baseline_play_arrow_24,
                        null
                    )
                )
            } else {
                musicService.resumeSong()
                pauseButton.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.baseline_pause_24,
                        null
                    )
                )
            }
        }

        /** Previous/Next song functionality */
        view.findViewById<ImageView>(R.id.ivRewindButton).setOnClickListener {
            musicService.previousSong()
            updateUi()
            pauseButton.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.baseline_pause_24,
                    null
                )
            )
        }
        view.findViewById<ImageView>(R.id.ivFastForwardButton).setOnClickListener {
            musicService.nextSong()
            updateUi()
            pauseButton.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.baseline_pause_24,
                    null
                )
            )
        }

        /** Progress bar functionality */
        val bar = view?.findViewById<SeekBar>(R.id.sbSongPositionBar)
        bar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    musicService.seekTo(progress)
                    activity?.runOnUiThread {
                        val currentTime = view.findViewById<TextView>(R.id.tvSongCurrentTime)
                        currentTime?.text = timeLabel(progress)
                    }
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        return view
    }

    override fun onDestroyView() {
        requireContext().unbindService(serviceConnection)
        handler.removeCallbacks(currentPositionTimer)
        super.onDestroyView()
    }

    /** Updates the UI components based on the current song. */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun updateUi() {
        val currentSong = musicService.songInfo()

        view?.findViewById<TextView>(R.id.tvSongTitle)?.text = currentSong?.title
        view?.findViewById<TextView>(R.id.tvArtistTitle)?.text = currentSong?.artist
        view?.findViewById<SeekBar>(R.id.sbSongPositionBar)?.max = currentSong?.duration!!
        view?.findViewById<TextView>(R.id.tvSongTotalTime)?.text = timeLabel(currentSong.duration)

//        val trackUri = currentSong.uri
//        val cr = context?.contentResolver

//        var bm: Bitmap? = null
//        if (cr != null) {
//            bm = trackUri.let { cr.loadThumbnail(it, Size(2048, 2048), null) }
//        }

        val image = view?.findViewById<ImageView>(R.id.ivSongImageView)

        if (image != null) {
            Glide.with(this)
                .load(currentSong.imageUri)
                .placeholder(R.drawable.image)
                .into(image)
        }
    }

    /** Generates a time label of mm:ss */
    private fun timeLabel(time: Int): String {
        var label = ""
        val min = time / 1000 / 60
        val sec = time / 1000 % 60

        label = "$min:"
        if (sec < 10) label += "0"
        label += sec

        return label
    }
}