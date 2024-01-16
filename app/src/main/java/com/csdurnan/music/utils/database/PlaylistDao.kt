package com.csdurnan.music.utils.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.csdurnan.music.dc.Playlist
import com.csdurnan.music.dc.PlaylistSongCrossRef
import com.csdurnan.music.dc.PlaylistWithSongs
import com.csdurnan.music.dc.Song

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM songs")
    fun getSongs(): List<Song>

    @Query("SELECT * FROM playlists")
    fun getPlaylists(): LiveData<List<Playlist>>

    @Transaction
    @Query("SELECT * FROM playlists WHERE playlist_id = :id")
    fun getSongsFromPlaylist(id: Long): LiveData<PlaylistWithSongs>

    @Query("UPDATE playlists SET songCount = songCount + 1 WHERE playlist_id = :id")
    suspend fun incrementPlaylistSongCount(id: Long)

    @Query("UPDATE playlists SET songCount = songCount - 1 WHERE playlist_id = :id")
    suspend fun decrementPlaylistSongCount(id: Long)

    @Upsert
    suspend fun upsertSong(song: Song): Long

    @Upsert
    suspend fun upsertPlaylist(playlist: Playlist): Long

    @Transaction
    @Upsert
    suspend fun upsertPlaylistSongCrossRef(playlistSongCrossRef: PlaylistSongCrossRef): Long


    @Delete
    suspend fun deleteSong(song: Song)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlaylistSongCrossRef(playlistSongCrossRef: PlaylistSongCrossRef)

    @Transaction
    suspend fun deleteSongFromPlaylist(playlistSongCrossRef: PlaylistSongCrossRef) {
        decrementPlaylistSongCount(playlistSongCrossRef.playlistId)
        deletePlaylistSongCrossRef(playlistSongCrossRef)
    }

    @Transaction
    suspend fun insertPlaylistAndSongs(playlist: Playlist) {
        val playlistId = upsertPlaylist(playlist)
        for (song in playlist.songs!!) {
            upsertSong(song)
            upsertPlaylistSongCrossRef(PlaylistSongCrossRef(playlistId, song.id))
        }
    }

    @Transaction
    suspend fun insertSongToPlaylist(playlistId: Long, song: Song) {
        upsertSong(song)
        upsertPlaylistSongCrossRef(PlaylistSongCrossRef(playlistId, song.id))
        incrementPlaylistSongCount(playlistId)
    }
}