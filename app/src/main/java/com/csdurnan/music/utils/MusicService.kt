package com.csdurnan.music.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.csdurnan.music.dc.Song
import java.util.LinkedList
import java.util.Queue

class MusicService :
    Service(),
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener,
    AudioManager.OnAudioFocusChangeListener {

    /**
     * Sets up the AudioManager, responsible for managing audio in Android.
     * Allows the app to request audio focus from other apps and vice versa.
     */
    private lateinit var audioManager: AudioManager

    override fun onAudioFocusChange(focusState: Int) {
        /** When the audio focus of the system is updated. */
        when (focusState) {
            /** The application is now the sole source of audio the user is listening to. */
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (!isPaused()) {
                    mediaPlayer.start()
                }
                mediaPlayer.setVolume(1.0F, 1.0F)
            }

            /** Lost audio focus for an indefinite amount of time. */
            AudioManager.AUDIOFOCUS_LOSS -> {
                mediaPlayer.stop()
                mediaPlayer.release()
            }

            /**
             * Lost focus for a short time so we don't release player.
             * Most likely due to alarm or call. */
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                mediaPlayer.pause()
            }

            /** Lost focus for a short time but can play at a lower volume. */
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                mediaPlayer.setVolume(0.1F, 0.1F)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestAudioFocus(): Boolean {
        var result = audioManager.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )

        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }


    /**
     * Sets up the MusicService itself, responsible for playing music within the app.
     */
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var songsMap: MutableMap<Long, Song>
    private var songPosition = 0L
    private var song: Song? = null
    private var songQueue: Queue<Long> = LinkedList()
    private var isPaused: Boolean = false

    private val musicBinder: IBinder = MusicBinder(this)

    override fun onBind(p0: Intent?): IBinder = musicBinder

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUnbind(intent: Intent?): Boolean {
        mediaPlayer.stop()
        mediaPlayer.release()
        audioManager.abandonAudioFocus(this)
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPrepared(p0: MediaPlayer?) {
        if (requestAudioFocus()) {
            mediaPlayer.start()
            Log.i("MusicService.kt", "Audio focus granted.")
        } else {
            Log.e("MusicService.kt", "Unable to request audio focus.")
        }
    }

    override fun onCreate() {
        super.onCreate()

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mediaPlayer = MediaPlayer()
        initializeMusicPlayer()
    }

    private fun initializeMusicPlayer() {
        // MediaPlayer will remain active while screen is turned off.
        mediaPlayer.setWakeMode(
            applicationContext,
            PowerManager.PARTIAL_WAKE_LOCK
        )

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        mediaPlayer.setAudioAttributes(audioAttributes)

        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setOnErrorListener(this)
    }

    fun setList(songList: MutableMap<Long, Song>) {
        this.songsMap = songList
    }

    /**
     * MediaPlayer functions.
     */
    fun setSong(songIndex: Long) {
        songPosition = songIndex
        song = songsMap[songPosition]
    }

    fun playSong() {
        val songUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            songPosition
        )

        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(applicationContext, songUri)
        } catch (ex: Exception) {
            Log.e("MusicService.kt", "Error setting data source")
        }

        mediaPlayer.prepare()
        isPaused = false
    }

    fun pauseSong() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            isPaused = true
        }
    }

    fun resumeSong() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    fun previousSong() {
        songPosition--
        mediaPlayer.stop()
        playSong()
        sendBroadcast(Intent("UPDATE_UI"))
    }

    fun nextSong() {
        if (songQueue.isNotEmpty()) {
            songPosition = songQueue.remove()
            mediaPlayer.stop()
            playSong()
        } else if (songsMap[songPosition++] != null){
            songPosition++
            mediaPlayer.stop()
            playSong()
        }
        sendBroadcast(Intent("UPDATE_UI"))
    }

    fun seekTo(progress: Int) = mediaPlayer.seekTo(progress)

    fun songInfo(): Song? = songsMap[songPosition]

    fun isPlaying(): Boolean = mediaPlayer.isPlaying

    fun isPaused(): Boolean = isPaused

    fun currentPosition(): Int = mediaPlayer.currentPosition

    fun addToQueue(songIndex: Long) = songQueue.add(songIndex)


    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        return false
    }

    override fun onCompletion(p0: MediaPlayer?) {
        if (songsMap[songPosition++] != null) {
            nextSong()
        } else {
            mediaPlayer.stop()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun notificationBuilder() {
        val CHANNEL_ID = "MUSICBYNOODLES"
        val name = "MusicByNoodles"
        val descriptionText = "Music player"
        val importance = NotificationManager.IMPORTANCE_MIN
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.description = descriptionText

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

        //val builder = NotificationCompat.Builder(this, CHANNEL_ID)
    }

}