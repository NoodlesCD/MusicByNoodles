package com.csdurnan.music.dc

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
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