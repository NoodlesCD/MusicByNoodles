package com.csdurnan.music.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.csdurnan.music.utils.ContentManagement
import com.csdurnan.music.MainNavDirections
import com.csdurnan.music.R
import com.csdurnan.music.dc.Album
import com.csdurnan.music.dc.PlaybackStatus
import com.csdurnan.music.dc.Song
import com.csdurnan.music.ui.albums.AllAlbumsAdapter
import com.csdurnan.music.ui.albums.AllAlbumsDirections
import com.csdurnan.music.ui.albums.currentAlbum.CurrentAlbumAdapter
import com.csdurnan.music.ui.albums.currentAlbum.CurrentAlbumDirections
import com.csdurnan.music.ui.songs.AllSongsAdapter
import com.csdurnan.music.ui.songs.AllSongsDirections
import com.csdurnan.music.ui.songs.currentSong.SongSelectorViewModel
import com.csdurnan.music.utils.musicService.MainActivityCurrentSongBarCallback
import com.csdurnan.music.utils.musicService.MusicBinder
import com.csdurnan.music.utils.musicService.MusicService
import com.csdurnan.music.utils.musicService.UpdateUiBroadcastReceiver
import com.google.android.material.bottomnavigation.BottomNavigationView
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity :
    AppCompatActivity(),
    MainActivityCurrentSongBarCallback,
    AllSongsAdapter.OnSongsItemClickListener,
    AllAlbumsAdapter.OnAllAlbumsItemClickListener,
    CurrentAlbumAdapter.OnAlbumItemClickListener {

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

    @RequiresApi(Build.VERSION_CODES.S)
    private fun songPicked(song: Int) {
        musicService!!.setSong(song.toLong())
        musicService!!.playSong()
        updateCurrentSongBarUi()
    }

    /** Updates the UI whenever a new song is played.. */
    @RequiresApi(Build.VERSION_CODES.S)
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
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (playIntent == null) {
                playIntent = Intent(this, MusicService::class.java)
                bindService(playIntent!!, musicConnection, BIND_AUTO_CREATE)
                startService(playIntent)
            }
            Log.i("MainActivity.kt", "Android permissions granted.")
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1)
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
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.navigation)
        bottomNavigation.setupWithNavController(navController)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.playlists -> {
                    navController.navigate(R.id.playlists)
                    true
                }
                R.id.artists -> {
                    navController.navigate(R.id.artists)
                    true
                }
                R.id.albums -> {
                    navController.navigate(R.id.albums)
                    true
                }
                R.id.songs -> {
                    navController.navigate(R.id.songs)
                    true
                }
                else -> false
            }
        }

        /** Sets songBarVisibility based on destination. See [songBarVisibility] function. */
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.currentSong) {
                songBarVisibility(View.GONE)
                findViewById<ImageView>(R.id.current_song).visibility = View.VISIBLE
            } else {
                songBarVisibility(View.VISIBLE)
                findViewById<ImageView>(R.id.current_song).visibility = View.GONE
                Handler(Looper.getMainLooper()).post(currentPositionTimer)
            }

            when (destination.id) {
                R.id.songs -> setMenuHeader(R.string.songs, View.VISIBLE)
                R.id.albums -> setMenuHeader(R.string.albums, View.VISIBLE)
                R.id.artists -> setMenuHeader(R.string.artists, View.VISIBLE)
                R.id.playlists -> setMenuHeader(R.string.playlists, View.VISIBLE)
                else -> setMenuHeader(null, View.GONE)
            }
        }

        val sleep = findViewById<ImageView>(R.id.iv_sleep_button)
        sleep.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.WrapContentDialog)
            val dialogView = this.layoutInflater.inflate(R.layout.dialog_sleep, null)
            val dialog = builder.setView(dialogView).create()
            dialogView.findViewById<TextView>(R.id.tv_sleep_dismiss).setOnClickListener {
                dialog.dismiss()
            }
            dialogView.findViewById<TextView>(R.id.tv_sleep_confirm).setOnClickListener {
                val sleepTimeInput = dialogView.findViewById<EditText>(R.id.et_sleep_timer)

                if (sleepTimeInput.text.isNotBlank()) {
                    Toast.makeText(this,"Timer set for ${sleepTimeInput.text} minutes", Toast.LENGTH_SHORT).show()
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(TimeUnit.MINUTES.toMillis(sleepTimeInput.text.toString().toLong()))
                        musicService?.stop()
                    }

                    dialog.dismiss()
                }
            }

            val decorView = window.decorView
            val rootView = decorView.findViewById(R.id.nav_host_fragment) as ViewGroup
            val windowBackground = decorView.background

            val dialogBlurView = dialogView.findViewById<BlurView>(R.id.dialog_sleep_timer)
            dialogBlurView.setupWith(rootView, RenderScriptBlur(this))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(3f)

            dialog.show()
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
            if (musicService?.playbackStatus == PlaybackStatus.PLAYING) {
                musicService?.pauseSong()
                playPauseButton?.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.controls_play,
                        null
                    )
                )
            } else {
                musicService?.resumeSong()
                playPauseButton.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.controls_pause,
                        null
                    )
                )
            }
        }

        val decorView = window.decorView
        val rootView = decorView.findViewById(R.id.nav_host_fragment) as ViewGroup
        val windowBackground = decorView.background

        val currentSongBarBlurView = findViewById<BlurView>(R.id.linearLayout3)
        currentSongBarBlurView.setupWith(rootView, RenderScriptBlur(this))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(3f)

        val headerBlurView = findViewById<BlurView>(R.id.header_bar)
        headerBlurView.setupWith(rootView, RenderScriptBlur(this))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(3f)
    }


    /**
     * Updates the UI information on the currentSongBar.
     * currentSongBar displays information about the currently playing song.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun updateCurrentSongBarUi() {
        val currentSong = musicService!!.songInfo()

        val background = findViewById<ImageView>(R.id.current_song)
        if (background != null) {
            Glide.with(this)
                .load(musicService?.songInfo()?.imageUri)
                .placeholder(R.drawable.artwork_placeholder)
                .into(background)
        }

        background.setRenderEffect(
            RenderEffect.createBlurEffect(
                50.0f, 50.0f, Shader.TileMode.CLAMP
            )
        )

//        val currentSongBackground = findViewById<ImageView>(R.id.current_song_bar_bg)
//        if (background != null) {
//            Glide.with(this)
//                .load(musicService?.songInfo()?.imageUri)
//                .placeholder(R.drawable.artwork_placeholder)
//                .apply(RequestOptions.bitmapTransform(BlurTransformation(25,3)))
//                .into(background)
//        }

        val currentSongTitle = findViewById<TextView>(R.id.tv_current_song_title)
        currentSongTitle.text = currentSong?.title
        val currentSongArtist = findViewById<TextView>(R.id.tv_current_song_artist)
        currentSongArtist.text = currentSong?.artist

        val currentSongImg = findViewById<ImageView>(R.id.iv_current_song_image)

        if (currentSongImg != null) {
            Glide.with(this)
                .load(currentSong?.imageUri)
                .placeholder(R.drawable.artwork_placeholder)
                .into(currentSongImg)
        }
    }

    /**
     * Sets the visibility of the currentSongBar.
     * Is hidden whenever viewing the CurrentSong fragment.
     */
    private fun songBarVisibility(visibility: Int) {
        val currentSongBar = findViewById<ConstraintLayout>(R.id.cl_current_song_bar)
        if (musicService != null && musicBound) {
            if (visibility == View.VISIBLE && (musicService?.playbackStatus != PlaybackStatus.STOPPED)) {
                currentSongBar.visibility = View.VISIBLE
            } else if (visibility == View.GONE) {
                currentSongBar.visibility = visibility
            }
        }
    }

    private fun setMenuHeader(stringId: Int?, visibility: Int) {
        if (stringId != null) {
            findViewById<TextView>(R.id.tv_header).text = getString(stringId)
        }
        findViewById<BlurView>(R.id.header_bar).visibility = visibility
    }

    /** Updates the currentSongBar every second with the current position of the song */
    private val currentPositionTimer = object : Runnable {
        override fun run() {
            runOnUiThread {
                if (musicBound) {
                    if (musicService != null) {
                        if (musicService?.playbackStatus == PlaybackStatus.PLAYING) {
                            val bar = findViewById<ProgressBar>(R.id.pb_current_song_progress)
                            bar?.max = musicService?.songInfo()?.duration!!
                            bar?.progress = musicService?.currentPosition()!!
                        }
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
                R.id.song_list_pop_add_playlist -> {
                    val action = AllSongsDirections.actionGlobalNewPlaylist(arrayOf(song))
                    navController.navigate(action)
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

    /**
     * Implements the function within [CurrentAlbumAdapter.OnAlbumItemClickListener]
     * Used within the CurrentAlbum fragment.
     * Provides functionality for an additional action list for each song.
     */
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
            R.id.song_list_pop_add_playlist -> {
                val action = CurrentAlbumDirections.actionGlobalNewPlaylist(arrayOf(song))
                navController.navigate(action)
            }
            R.id.song_list_popup_artist -> {
                val action = CurrentAlbumDirections.actionGlobalCurrentArtist(cm.artistsList[song.artistId]!!)
                navController.navigate(action)
            }
        }
    }

    /**
     * Implements the function within [AllAlbumsAdapter.OnAllAlbumsItemClickListener]
     * Used within the AllAlbums fragment.
     * Provides functionality for an additional action list for each album.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onAllAlbumsItemClick(position: Int, album: Album) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val cm = ContentManagement(applicationContext.contentResolver)

        when (position) {
            R.id.song_list_popup_add_queue -> {
                for (song in album.songs) {
                    musicService?.addToQueue(song.id)
                }
            }
            R.id.song_list_popup_add_playlist -> {
                val action = AllAlbumsDirections.actionGlobalNewPlaylist(album.songs.toTypedArray())
                navController.navigate(action)
            }
            R.id.song_list_popup_artist -> {
                val action = AllAlbumsDirections.actionGlobalCurrentArtist(cm.artistsList[album.artistId]!!)
                navController.navigate(action)
            }
        }
    }
}