package com.csdurnan.music.utils.musicService

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.csdurnan.music.ui.songs.currentSong.CurrentSongChangeCallback
import com.csdurnan.music.utils.musicService.MainActivityCurrentSongBarCallback

class UpdateUiBroadcastReceiver(
    private val currentSongCallback: CurrentSongChangeCallback?,
    private val mainActivityBarCallback: MainActivityCurrentSongBarCallback?
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "UPDATE_UI") {
            if (currentSongCallback != null) {
                currentSongCallback.onAction()
            } else {
                mainActivityBarCallback?.onAction()
            }
        }
    }
}