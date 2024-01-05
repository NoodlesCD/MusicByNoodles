package com.csdurnan.music.utils.database

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter

class PlaylistDatabaseConverters {
    @TypeConverter
    fun fromUriToString(uri: Uri): String = uri.toString()

    @TypeConverter
    fun fromStringToUri(string: String): Uri = string.toUri()
}