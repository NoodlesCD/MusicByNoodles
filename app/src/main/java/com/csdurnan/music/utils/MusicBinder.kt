package com.csdurnan.music.utils

import android.os.Binder

class MusicBinder(private val musicService: MusicService) : Binder() {
    val getService: MusicService
    get() = musicService
}
