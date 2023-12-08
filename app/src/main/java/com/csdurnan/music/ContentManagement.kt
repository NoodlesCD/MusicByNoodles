package com.csdurnan.music

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.csdurnan.music.dc.Album
import com.csdurnan.music.dc.Artist
import com.csdurnan.music.dc.Song
import com.csdurnan.music.ui.songs.AllSongsPagingSource
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.Q)
class ContentManagement(contentResolver: ContentResolver) {
    var artistsList: MutableMap<Long, Artist> = mutableMapOf()
    var albumsList: MutableMap<Long, Album> = mutableMapOf()
    var songsList: MutableList<Song> = mutableListOf()
    var songsMap: MutableMap<Long, Song> = mutableMapOf()

    init {
        runBlocking {
            launch {
                val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                // Creates an instance of the ContentResolver. The contentResolver property is part of the 'Context' class
                val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)

                when {
                    cursor == null -> {
                        // query failed
                        println("query failed")
                    }
                    !cursor.moveToFirst() -> {
                        println("no media on device")
                        // no media on device
                    }
                    else -> {
                        val idColumn: Int  = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                        val titleColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                        val albumColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
                        val artistColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                        val cdTrackNumberColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.CD_TRACK_NUMBER)
                        val albumIdColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                        val artistIdColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)
                        val durationColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)

                        do {
                            val id = cursor.getLong(idColumn)
                            val title = cursor.getString(titleColumn)
                            val album = cursor.getString(albumColumn)
                            val artist = cursor.getString(artistColumn)
                            val cdTrackNumber = cursor.getInt(cdTrackNumberColumn)
                            val albumId = cursor.getLong(albumIdColumn)
                            val artistId = cursor.getLong(artistIdColumn)
                            val duration = cursor.getInt(durationColumn)

                            val trackUri =
                                ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                            val cr = contentResolver

//                            var bm: Bitmap? = null
//                            if (cr != null) {
//                                try {
//                                    bm = cr.loadThumbnail(trackUri, android.util.Size(2048, 2048), null)
//                                } catch (ex: FileNotFoundException) {
//
//                                }
//
//                            }

                            val sArtworkUri: Uri = Uri.parse("content://media/external/audio/albumart")
                            val imgUri: Uri = ContentUris.withAppendedId(
                                sArtworkUri,
                                albumId
                            )

                            val newSong = Song(id, title, album, artist, cdTrackNumber, albumId, artistId, duration, trackUri, imgUri)

                            // If the artist does not already exist in the list, create one.
                            if (artistsList[artistId] == null) {
                                val newAlbum = mutableListOf(Album(album, artist, mutableListOf(newSong), imgUri))
                                artistsList[artistId] = Artist(artist, newAlbum, 1)
                            }
                            // If the artist already exists.
                            else {
                                var existingAlbum = false

                                // Check if album exists
                                for (i in artistsList.getValue(artistId).albums.indices) {
                                    if (artistsList.getValue(artistId).albums[i].albumTitle == album) {
                                        artistsList.getValue(artistId).albums[i].songs.add(newSong)
                                        existingAlbum = true
                                        break
                                    }
                                }

                                if (!existingAlbum) {
                                    var newAlbum = Album(album, artist, mutableListOf(newSong), imgUri)
                                    artistsList.getValue(artistId).albums.add(newAlbum)
                                }

                                artistsList.getValue(artistId).songCount++
                            }

//                    if (albumsList[album] == null) {
//                        albumsList[album] = Album(album, artist, mutableListOf(newSong))
//                    }

                            if (albumsList[albumId] == null) {
                                albumsList[albumId] = Album(album, artist, mutableListOf(newSong), imgUri)
                            } else {
                                albumsList[albumId]?.songs?.add(newSong)
                            }

                            songsList.add(newSong)
                            songsMap[id] = newSong
                        } while (cursor.moveToNext())

                    }
                }
                cursor?.close()
            }
        }
    }

    val artists = ArrayList<Artist>(artistsList.values)
    val albums = ArrayList<Album>(albumsList.values)

    val songs = ArrayList<Song>(songsList)
    val songsSorted = songs.sortedBy { it.title }

    fun allSongsPagingSource(): AllSongsPagingSource {
        return AllSongsPagingSource(songsSorted)
    }

}