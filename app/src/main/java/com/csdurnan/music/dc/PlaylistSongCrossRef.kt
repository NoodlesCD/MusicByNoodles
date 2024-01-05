package com.csdurnan.music.dc

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "playlist_song_crossrefs",
    primaryKeys = ["playlist_id", "song_id"]    )
data class PlaylistSongCrossRef(
    @ColumnInfo(name = "playlist_id")
    val playlistId: Long,

    @ColumnInfo(name = "song_id")
    val songId: Long
)
