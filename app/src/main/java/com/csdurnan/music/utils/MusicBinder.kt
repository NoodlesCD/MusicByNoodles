package com.csdurnan.music.utils

import android.os.Binder

/**
 * Allows components of the application to create a connection to the MusicService.
 */
class MusicBinder(private val musicService: MusicService) : Binder() {
    val getService: MusicService
    get() = musicService
}
