package com.csdurnan.music.activities

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Size
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.csdurnan.music.ContentManagement
import com.csdurnan.music.MainNavDirections
import com.csdurnan.music.R
import com.csdurnan.music.adapters.AllSongsAdapter
import com.csdurnan.music.dc.Song
import com.csdurnan.music.fragments.AllSongsDirections
import com.csdurnan.music.utils.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), MainActivityCurrentSongBarCallback, AllSongsAdapter.OnSongsItemClickListener {

    /** Sets up the musicService whenever a song is selected for the first time.. */
    private var musicService: MusicService? = null
    private var playIntent: Intent? = null
    private var musicBound = false
    private val handler = Handler()

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
    private val viewModel: ItemViewModel by viewModels()

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
            println("------ permission granted")
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            finish();
            startActivity(intent);
            println("------ permission not granted")
        }





        setContentView(R.layout.activity_main)

        /** Observes for whenever a user selects a song. */
        viewModel.selectedItem.observe(this, Observer { item ->
            songPicked(item)
        })

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
            } else {
                songBarVisibility(View.VISIBLE)
                handler.post(currentPositionTimer)
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
            if (currentSong.uri != null) {
                val trackUri = currentSong.uri
                val cr = contentResolver

                var bm: Bitmap? = null
                if (cr != null) {
                    bm = trackUri?.let { cr.loadThumbnail(it, Size(1024, 1024), null) }
                }

                currentSongImg.setImageBitmap(bm)
                currentSongImg.scaleType =
                    ImageView.ScaleType.FIT_CENTER
            }
        }
    }

    /**
     * Sets the visibility of the currentSongBar.
     * Is hidden whenever viewing the CurrentSong fragment.
     */
    private fun songBarVisibility(visibility: Int) {
        val currentSongBar = findViewById<ConstraintLayout>(R.id.cl_current_song_bar)
        if (visibility == View.VISIBLE && musicService?.isPlaying() == true) {
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
                        var bar = findViewById<ProgressBar>(R.id.pb_current_song_progress)
                        bar?.max = musicService?.songInfo()?.duration!!
                        bar?.progress = musicService?.currentPosition()!!
                    }
                }
            }

            handler.postDelayed(this, 1000)
        }
    }

    /**
     * Implements the function within [AllSongsAdapter.onSongsItemClickListener]
     * Used within the AllSongs fragment.
     * Provides functionality for an additional action list for each song.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onItemClick(position:Int, song: Song) {
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
                else -> false
            }
        }
}