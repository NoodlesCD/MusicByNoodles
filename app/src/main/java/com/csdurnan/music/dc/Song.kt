package com.csdurnan.music.dc

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "songs")
@Parcelize
data class Song(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "song_id")
    val id: Long,
    val title: String,
    val album: String,
    val artist: String,
    val cdTrackNumber: Int,
    val albumId: Long,
    val artistId: Long,
    val duration: Int,
    val uri: Uri,
    val imageUri: Uri,
) : Parcelable