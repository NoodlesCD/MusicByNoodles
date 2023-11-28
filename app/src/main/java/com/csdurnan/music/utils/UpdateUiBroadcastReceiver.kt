package com.csdurnan.music.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

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