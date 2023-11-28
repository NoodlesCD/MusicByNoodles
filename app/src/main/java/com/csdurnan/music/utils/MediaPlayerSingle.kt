package com.csdurnan.music.utils

import android.content.ContentUris
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.provider.MediaStore
import android.widget.SeekBar
import android.widget.TextView
import com.csdurnan.music.R
import com.csdurnan.music.dc.Song
import java.util.*

object MediaPlayerSingle {
    lateinit var mediaPlayer: MediaPlayer

    fun getPlayer(): MediaPlayer {
        if (mediaPlayer != null) mediaPlayer.reset()
        mediaPlayer = MediaPlayer()
        return mediaPlayer
    }



//    var mediaPlayer = MediaPlayer()
//    val bar = view.findViewById<SeekBar>(R.id.sbSongPositionBar)
//    val timer = Timer()
//    val currentTime = view.findViewById<TextView>(R.id.tvSongCurrentTime)
//
//    when {
//        cursor == null -> {
//            // query failed
//            println("query failed")
//        }
//        !cursor.moveToFirst() -> {
//            println("no media on device")
//            // no media on device
//        }
//        else -> {
//            val idColumn: Int  = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
//            val titleColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
//            val albumColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
//            val artistColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
//            val cdTrackNumberColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.CD_TRACK_NUMBER)
//            val albumIdColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
//            val durationColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
//
//            do {
//                val id = cursor.getLong(idColumn)
//                val title = cursor.getString(titleColumn)
//                val album = cursor.getString(albumColumn)
//                val artist = cursor.getString(artistColumn)
//                val cdTrackNumber = cursor.getInt(cdTrackNumberColumn)
//                val albumId = cursor.getInt(albumIdColumn)
//                val duration = cursor.getInt(durationColumn)
//
//                val newSong = Song(id, title, album, artist, cdTrackNumber, albumId)
//
//                view.findViewById<TextView>(R.id.tvSongTitle).text = title
//                view.findViewById<TextView>(R.id.tvArtistTitle).text = artist
//                view.findViewById<TextView>(R.id.tvSongTotalTime).text = timeLabel(durationColumn)
//
//                songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
//
//                println(newSong.toString())
//
//                if (mediaPlayer.isPlaying) mediaPlayer.release()
//
//                println("\n\n\n ${songUri} \n\n\n")
//                mediaPlayer = MediaPlayer().apply {
//                    setAudioAttributes(
//                        AudioAttributes.Builder()
//                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                            .setUsage(AudioAttributes.USAGE_MEDIA)
//                            .build()
//                    )
//                    context?.let { setDataSource(it, songUri) }
//                    prepare()
//                    start()
//
//                    bar.max = duration
//                    val update = object : TimerTask() {
//                        override fun run() {
//                            bar.progress = currentPosition
//                            activity?.runOnUiThread {
//                                currentTime.text = timeLabel(currentPosition)
//                            }
//
//                        }
//                    }
//
//                    timer.scheduleAtFixedRate(update, 0, 1000)
//
//
//                }
//
//
//            } while (cursor.moveToNext())
//
//        }
//    }
//    cursor?.close()
}