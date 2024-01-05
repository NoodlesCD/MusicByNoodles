package com.csdurnan.music.dc

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.csdurnan.music.utils.PlaylistDatabase

data class PlaylistWithSongs(
    @Embedded val playlist: Playlist,
    @Relation(
        parentColumn = "playlist_id",
        entityColumn = "song_id",
        associateBy = Junction(PlaylistSongCrossRef::class)
    )
    val songs: List<Song>
)
