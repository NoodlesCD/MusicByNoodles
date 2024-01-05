package com.csdurnan.music.dc

import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "playlists")
data class Playlist @JvmOverloads constructor (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlist_id")
    val id: Long?,
    val name: String,
    val songCount: Int,
    val albumUri: Uri,
    @Ignore val songs: List<Song>? = emptyList()
) : Parcelable