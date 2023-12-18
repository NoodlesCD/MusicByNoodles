package com.csdurnan.music.utils

import androidx.room.Database
import androidx.room.RoomDatabase
import com.csdurnan.music.dc.Playlist

@Database(
    entities = [Playlist::class],
    version = 1
)
abstract class PlaylistDatabase: RoomDatabase() {
    abstract val dao: PlaylistDao
}