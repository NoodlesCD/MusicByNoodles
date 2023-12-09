package com.csdurnan.music.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.csdurnan.music.ContentManagement
import com.csdurnan.music.MainNavDirections
import com.csdurnan.music.R
import com.csdurnan.music.adapters.AllSongsAdapter
import com.csdurnan.music.adapters.CurrentAlbumAdapter
import com.csdurnan.music.dc.Song
import com.csdurnan.music.ui.currentSong.SongSelectorViewModel
import com.csdurnan.music.ui.songs.AllSongsDirections
import com.csdurnan.music.utils.MainActivityCurrentSongBarCallback
import com.csdurnan.music.utils.MusicBinder
import com.csdurnan.music.utils.MusicService
import com.csdurnan.music.utils.UpdateUiBroadcastReceiver
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), MainActivityCurrentSongBarCallback, AllSongsAdapter.OnSongsItemClickListener, CurrentAlbumAdapter.OnAlbumItemClickListener {

    /** Sets up the musicService whenever a song is selected for the first time.. */
    private var musicService: MusicService? = null
    private var playIntent: Intent? = null
    private var musicBound = false

    @RequiresApi(Build.VERSION_CODES.Q)
    private val musicConnection: ServiceConnection = object : ServiceConnection {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicBinder

            musicService = binder.getService
            musicService!!.setList(ContentManagement(applicationContext.contentResolver).songsMap)
            musicBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicBound = false
        }
    }

    /** Updates the musicService with the selected song. */
    private val viewModel: SongSelectorViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun songPicked(song: Int) {
        musicService!!.setSong(song.toLong())
        musicService!!.playSong()
        updateCurrentSongBarUi()
    }

    /** Updates the UI whenever a new song is played.. */
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onAction() {
        updateCurrentSongBarUi()
    }


    /**
     * MainActivity function implementations.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        stopService(playIntent)
        musicService = null
        super.onDestroy()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted
            // Perform the operation that requires this permission
            if (playIntent == null) {
                playIntent = Intent(this, MusicService::class.java)
                bindService(playIntent, musicConnection, BIND_AUTO_CREATE)
                startService(playIntent)
            }
            Log.i("MainActivity.kt", "Android permissions granted.")
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            finish()
            startActivity(intent)
            Log.i("MainActivity.kt", "Android permissions not granted.")
        }

        setContentView(R.layout.activity_main)

        /** Observes for whenever a user selects a song. */
        viewModel.selectedItem.observe(this) { item ->
            songPicked(item)
        }

        /** Sets up a broadcastReceiver which the musicService uses to notify of a needed UI change. */
        val broadcastReceiver = UpdateUiBroadcastReceiver(null, this)
        registerReceiver(broadcastReceiver, IntentFilter("UPDATE_UI"))

        /** Sets up the navHostFragment and bottomNavigationBar for navigation. */
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val headerText = findViewById<TextView>(R.id.tv_header)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.navigation)
        bottomNavigation.setupWithNavController(navController)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.artists -> {
                    navController.navigate(R.id.artists)
                    headerText.text = getString(R.string.artists)
                    true
                }
                R.id.albums -> {
                    navController.navigate(R.id.albums)
                    headerText.text = getString(R.string.albums)
                    true
                }
                R.id.songs -> {
                    navController.navigate(R.id.songs)
                    headerText.text = getString(R.string.songs)
                    true
                }
                else -> false

            }
        }

        /** Sets songBarVisibility based on destination. See [songBarVisibility] function. */
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.currentSong) {
                songBarVisibility(View.GONE)
            } else {
                songBarVisibility(View.VISIBLE)
                Handler(Looper.getMainLooper()).post(currentPositionTimer)
            }
            if (destination.id != R.id.artists
                && destination.id != R.id.albums
                && destination.id != R.id.songs) {
                headerText.text = ""
                findViewById<View>(R.id.v_top_shadow).visibility = View.GONE
            } else {
                findViewById<View>(R.id.v_top_shadow).visibility = View.VISIBLE
            }
        }

        /** Sets up navigation from the currentSongBar to the CurrentSong fragment. */
        val currentSongBar = findViewById<ConstraintLayout>(R.id.cl_current_song_bar)
        currentSongBar.setOnClickListener {
            val songId = musicService?.songInfo()?.id

            if (songId != null) {
                val action = MainNavDirections.actionGlobalCurrentSong(0L)
                navController.navigate(action)
            }
        }

        val playPauseButton = findViewById<ImageView>(R.id.iv_current_song_play_pause)
        playPauseButton.setOnClickListener {
            if (musicService?.isPlaying() == true) {
                musicService?.pauseSong()
                playPauseButton?.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.baseline_play_arrow_24,
                        null
                    )
                )
            } else {
                musicService?.resumeSong()
                playPauseButton.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.baseline_pause_24,
                        null
                    )
                )
            }
        }
    }


    /**
     * Updates the UI information on the currentSongBar.
     * currentSongBar displays information about the currently playing song.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun updateCurrentSongBarUi() {
        val currentSong = musicService!!.songInfo()

        val currentSongTitle = findViewById<TextView>(R.id.tv_current_song_title)
        currentSongTitle.text = currentSong?.title
        val currentSongArtist = findViewById<TextView>(R.id.tv_current_song_artist)
        currentSongArtist.text = currentSong?.artist

        val currentSongImg = findViewById<ImageView>(R.id.iv_current_song_image)
        if (currentSong != null) {
            val trackUri = currentSong.uri
            val cr = contentResolver

            var bm: Bitmap? = null
            if (cr != null) {
                bm = trackUri.let { cr.loadThumbnail(it, Size(1024, 1024), null) }
            }

            currentSongImg.setImageBitmap(bm)
            currentSongImg.scaleType =
                ImageView.ScaleType.FIT_CENTER
        }
    }

    /**
     * Sets the visibility of the currentSongBar.
     * Is hidden whenever viewing the CurrentSong fragment.
     */
    private fun songBarVisibility(visibility: Int) {
        val currentSongBar = findViewById<ConstraintLayout>(R.id.cl_current_song_bar)
        if (visibility == View.VISIBLE && (musicService?.isPlaying() == true || musicService?.isPaused() == true)) {
            currentSongBar.visibility = View.VISIBLE
        } else if (visibility == View.GONE) {
            currentSongBar.visibility = visibility
        }
    }

    /** Updates the currentSongBar every second with the current position of the song */
    private val currentPositionTimer = object : Runnable {
        override fun run() {
            runOnUiThread {
                if (musicBound) {
                    if (musicService?.isPlaying() == true) {
                        val bar = findViewById<ProgressBar>(R.id.pb_current_song_progress)
                        bar?.max = musicService?.songInfo()?.duration!!
                        bar?.progress = musicService?.currentPosition()!!
                    }
                }
            }

            Handler(Looper.getMainLooper()).postDelayed(this, 1000)
        }
    }

    /**
     * Implements the function within [AllSongsAdapter.onSongsItemClickListener]
     * Used within the AllSongs fragment.
     * Provides functionality for an additional action list for each song.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onSongItemClick(position:Int, song: Song) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val cm = ContentManagement(applicationContext.contentResolver)

        when (position) {
                R.id.song_list_popup_add_queue -> {
                    musicService?.addToQueue(song.id)
                }
                R.id.song_list_popup_artist -> {
                    val action = AllSongsDirections.actionGlobalCurrentArtist(cm.artistsList[song.artistId]!!)
                    navController.navigate(action)
                }
                R.id.song_list_popup_album -> {
                    val action = AllSongsDirections.actionGlobalCurrentAlbum(cm.albumsList[song.albumId]!!)
                    navController.navigate(action)
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onAlbumItemClick(position: Int, song: Song) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val cm = ContentManagement(applicationContext.contentResolver)

        when (position) {
            R.id.song_list_popup_add_queue -> {
                musicService?.addToQueue(song.id)
            }
            R.id.song_list_popup_artist -> {
                val action = AllSongsDirections.actionGlobalCurrentArtist(cm.artistsList[song.artistId]!!)
                navController.navigate(action)
            }
        }
    }
}