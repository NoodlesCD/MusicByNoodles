package com.csdurnan.music.utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.csdurnan.music.dc.Playlist
import com.csdurnan.music.dc.PlaylistSongCrossRef
import com.csdurnan.music.dc.Song
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        Playlist::class,
        Song::class,
        PlaylistSongCrossRef::class],
    version = 1
)
@TypeConverters(PlaylistDatabaseConverters::class)
abstract class PlaylistDatabase: RoomDatabase() {
    abstract val dao: PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: PlaylistDatabase? = null
        fun getInstance(context: Context): PlaylistDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PlaylistDatabase::class.java,
                    "playlist_db"
                ).build().also { databaseInstance ->
                    INSTANCE = databaseInstance
                }
            }

        }
    }
}