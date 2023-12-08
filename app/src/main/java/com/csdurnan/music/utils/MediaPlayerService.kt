package com.csdurnan.music.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.session.MediaSessionManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.csdurnan.music.R
import com.csdurnan.music.activities.MainActivity
import com.csdurnan.music.dc.PlaybackStatus
import com.csdurnan.music.dc.Song
import java.io.BufferedInputStream
import java.io.FileInputStream

class MediaPlayerService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, AudioManager.OnAudioFocusChangeListener {
    /**
     * AUDIO MANAGER
     */
    private var audioManager: AudioManager? = null
    private lateinit var activeAudio: Song

    override fun onAudioFocusChange(focusState: Int) {

    }

    private fun requestAudioFocus(): Boolean {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        var result = audioManager?.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)

        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun removeAudioFocus(): Boolean {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager?.abandonAudioFocus(this)
    }

    /**
     * MEDIA SESSION
     */
    val ACTION_PLAY = "com.csdurnan.music.ACTION_PLAY"
    val ACTION_PAUSE = "com.csdurnan.music.ACTION_PAUSE"
    val ACTION_PREVIOUS = "com.csdurnan.music.ACTION_PREVIOUS"
    val ACTION_NEXT = "com.csdurnan.music.ACTION_NEXT"
    val ACTION_STOP = "com.csdurnan.music.ACTION_STOP"

    lateinit var mediaSessionManager: MediaSessionManager
    lateinit var mediaSession: MediaSessionCompat
    lateinit var transportControls: MediaControllerCompat.TransportControls

    private val NOTIFICATION_ID = 101
    var resumePosition = 0

    private fun initializeMediaSession() {
        if (::mediaSessionManager.isInitialized) return

        mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        mediaSession = MediaSessionCompat(applicationContext, "MusicPlayer")
        transportControls = mediaSession.controller.transportControls
        mediaSession.isActive = true
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

        updateMetaData()

        mediaSession.setCallback(object: MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()
                resumeMedia()
                buildNotification(PlaybackStatus.PLAYING)
            }

            override fun onPause() {
                super.onPause()
                pauseMedia()
                buildNotification(PlaybackStatus.PAUSED)
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                skipToNext()
                updateMetaData()
                buildNotification(PlaybackStatus.PLAYING)
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                skipToPrevious()
                updateMetaData()
                buildNotification(PlaybackStatus.PLAYING)
            }

            override fun onStop() {
                super.onStop()
                removeNotification()
                stopSelf()
            }

        })
    }

    fun updateMetaData() {
        var albumArt: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.image)

        mediaSession.setMetadata(MediaMetadataCompat.Builder()
            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio.album)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.title)
            .build())
    }

    fun skipToNext() {

    }

    fun skipToPrevious() {

    }

    fun buildNotification(playbackStatus: PlaybackStatus) {
        var notificationAction = android.R.drawable.ic_media_pause
        var playPauseAction: PendingIntent? = null

        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause
            playPauseAction = playbackAction(1)
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play
            playPauseAction = playbackAction(0)
        }

        var largeIcon: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.image)

        var notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setShowWhen(false)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken).setShowActionsInCompactView(0, 1, 2))
            //.setColor(resources.getColor(R.color.colorPrimary))
            .setLargeIcon(largeIcon)
            .setSmallIcon(android.R.drawable.stat_sys_headset)
            .setContentText(activeAudio.artist)
            .setContentTitle(activeAudio.album)
            .setContentInfo(activeAudio.title)
            .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3))
            .addAction(notificationAction, "pause", playPauseAction)
            .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2))

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    fun removeNotification() {
        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun playbackAction(actionNumber: Int): PendingIntent? {
        var playbackAction = Intent(this, MediaPlayerService::class.java)
        when (actionNumber) {
            0 -> {
                // Play
                playbackAction.action = ACTION_PLAY
                return PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
            1 -> {
                // Pause
                playbackAction.action = ACTION_PAUSE
                return PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
            2 -> {
                // Next
                playbackAction.action = ACTION_NEXT
                return PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
            3 -> {
                playbackAction.action = ACTION_PREVIOUS
                return PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
            else ->
                return null
        }
    }

    fun handleIncomingActions(playbackAction: Intent) {
        if (playbackAction.action == null) return

        var action = playbackAction.action

        when (action) {
            ACTION_PLAY -> transportControls.play()
            ACTION_PAUSE -> transportControls.pause()
            ACTION_NEXT -> transportControls.skipToNext()
            ACTION_PREVIOUS -> transportControls.skipToPrevious()
            ACTION_STOP -> transportControls.stop()
        }
    }

    /**
     * MEDIA PLAYER SERVICE
     */
    private var mediaPlayer: MediaPlayer? = null
    private var mediaFile: String = ""


    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private fun initializeMediaPlayer() {
        mediaPlayer = MediaPlayer().apply {
                    setOnPreparedListener(this@MediaPlayerService)
                    setOnCompletionListener(this@MediaPlayerService)
                    setOnErrorListener(this@MediaPlayerService)
                    reset()
        }
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setLegacyStreamType(AudioManager.STREAM_MUSIC)
            .build()

//        val song = contentResolver.openInputStream()
//        val inputStream = BufferedInputStream(song)
        mediaPlayer?.let {
            it.setAudioAttributes(audioAttributes)
            it.setDataSource(applicationContext, ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, activeAudio.id))
        }

        mediaPlayer!!.prepare()



//        if (mediaUri != null) {
//            try {
//                mediaPlayer = MediaPlayer().apply {
//                    setDataSource(mediaUri)
//                    setOnPreparedListener(this@MediaPlayerService)
//                    setOnCompletionListener(this@MediaPlayerService)
//                    setOnErrorListener(this@MediaPlayerService)
//                    prepareAsync()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
    }


    /**
     * Prepares the service to start.
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val extras = intent?.extras
        val song = extras?.getParcelable<Song>("MEDIA_URI")

        if (song != null) {
            activeAudio = song
            if (!requestAudioFocus()) stopSelf()

            initializeMediaSession()
            initializeMediaPlayer()
            buildNotification(PlaybackStatus.PLAYING)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            stopMedia()
            mediaPlayer!!.release()
        }
        removeAudioFocus()
        unregisterReceiver(playNewAudio)
    }

    //    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val mediaUri = intent?.getStringExtra("MEDIA_URI")
//        if (mediaUri != null) {
//            try {
//                mediaPlayer = MediaPlayer().apply {
//                    setDataSource(mediaUri)
//                    setOnPreparedListener(this@MediaPlayerService)
//                    setOnCompletionListener(this@MediaPlayerService)
//                    setOnErrorListener(this@MediaPlayerService)
//                    prepareAsync()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//        return START_STICKY
//    }

    /**
     * Run's once the service has been prepared and the MediaPlayer can now start up.
     */
    override fun onPrepared(mp: MediaPlayer?) {
        mediaPlayer?.start()
    }

    /**
     * Media options.
     */
    fun playMedia() {
        if (!mediaPlayer?.isPlaying!!) mediaPlayer!!.start()
    }

    fun stopMedia() {
        if (mediaPlayer?.isPlaying == true) mediaPlayer!!.stop()
    }

    fun pauseMedia() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer!!.pause()
            resumePosition = mediaPlayer?.currentPosition!!
        }
    }

    fun resumeMedia() {
        if (!mediaPlayer?.isPlaying!!) {
            mediaPlayer?.seekTo(resumePosition)
            mediaPlayer?.start()
        }
    }

    fun seekTo(progress: Int) {
        mediaPlayer?.seekTo(progress)
    }

    fun setMedia(song: Song) {
        stopMedia()
        activeAudio = song
        mediaPlayer?.reset()
        initializeMediaPlayer()
        initializeMediaSession()
        updateMetaData()
        buildNotification(PlaybackStatus.PLAYING)
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun currentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    /**
     * Stops the service.
     */
    override fun onCompletion(mp: MediaPlayer?) {
        stopMedia()
        stopSelf()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        return false
    }

    inner class LocalBinder : Binder() {
        fun getService(): MediaPlayerService {
            return this@MediaPlayerService
        }
    }

    private val binder = LocalBinder()


//    var becomingNoisyReceiver: BroadcastReceiver = object: BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            pauseMedia()
//            buildNotification(PlaybackStatus.PAUSED)
//        }
//    }
//
//    fun registerBecomingNoisyReceiver() {
//        var intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
//        registerReceiver(becomingNoisyReceiver, intentFilter)
//    }

    private var playNewAudio: BroadcastReceiver = object: BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onReceive(context: Context?, intent: Intent?) {
            val song = intent?.getParcelableExtra("MEDIA_URI", Song::class.java)
            if (song != null) {
                try {
                    stopMedia()
                    activeAudio = song
                    mediaPlayer?.reset()
                    initializeMediaPlayer()
                    updateMetaData()
                    buildNotification(PlaybackStatus.PLAYING)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

//    fun register_playNewAudio() {
//        var filter = IntentFilter(MainActivity.Broadcast_PLAY_NEW_AUDIO)
//        registerReceiver(playNewAudio, filter)
//    }

}


