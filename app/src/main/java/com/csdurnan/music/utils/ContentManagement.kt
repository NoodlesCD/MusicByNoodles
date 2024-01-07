package com.csdurnan.music.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.csdurnan.music.dc.Album
import com.csdurnan.music.dc.Artist
import com.csdurnan.music.dc.Song
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
                val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)

                when {
                    cursor == null -> Log.e("ContentManagement.kt", "ContentResolver query failed.")
                    !cursor.moveToFirst() -> Log.i("ContentManagement.kt", "ContentResolver failed to find any media on device.")
                    else -> {
                        val idColumn: Int  = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                        val titleColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                        val albumColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
                        val artistColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                        val albumIdColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                        val artistIdColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)
                        val durationColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
                        val cdTrackNumberColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.CD_TRACK_NUMBER)

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

                            val sArtworkUri: Uri = Uri.parse("content://media/external/audio/albumart")
                            val imgUri: Uri = ContentUris.withAppendedId(
                                sArtworkUri,
                                albumId
                            )

                            val newSong = Song(id, title, album, artist, cdTrackNumber, albumId, artistId, duration, trackUri, imgUri)

                            // If the artist does not already exist in the list, create one.
                            if (artistsList[artistId] == null) {
                                val newAlbum = mutableListOf(Album(album, artist, artistId, mutableListOf(newSong), imgUri))
                                artistsList[artistId] = Artist(artist, newAlbum, 1)
                            }
                            // If the artist already exists.
                            else {
                                var existingAlbum = false

                                // Check if album exists
                                for (i in artistsList.getValue(artistId).albums.indices) {
                                    if (artistsList.getValue(artistId).albums[i].title == album) {
                                        artistsList.getValue(artistId).albums[i].songs.add(newSong)
                                        existingAlbum = true
                                        break
                                    }
                                }

                                if (!existingAlbum) {
                                    var newAlbum = Album(album, artist, artistId, mutableListOf(newSong), imgUri)
                                    artistsList.getValue(artistId).albums.add(newAlbum)
                                }

                                artistsList.getValue(artistId).songCount++
                            }

                            if (albumsList[albumId] == null) {
                                albumsList[albumId] = Album(album, artist, artistId, mutableListOf(newSong), imgUri)
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

    val artists = ArrayList<Artist>(artistsList.values).sortedBy { it.name }
    val albums = ArrayList<Album>(albumsList.values).sortedBy { it.title }
    val songs = ArrayList<Song>(songsList).sortedBy { it.title }
}