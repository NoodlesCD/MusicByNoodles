package com.csdurnan.music.utils

import android.content.Context
import android.media.session.MediaSession
import android.service.controls.ControlsProviderService
import android.service.controls.ControlsProviderService.TAG
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

class MediaService(context: Context) {
//    class SessionCallback : MediaSessionCompat.Callback() {
//        override fun onPlay() {
//            super.onPlay()
//        }
//
//        override fun onPause() {
//            super.onPause()
//        }
//
//        override fun onSkipToPrevious() {
//            super.onSkipToPrevious()
//        }
//    }
//
//    init {
//        var mediaSession = MediaSessionCompat(context, TAG)
//        mediaSession.setMediaButtonReceiver(null)
//        var mediaStateBuilder = PlaybackStateCompat.Builder()
//            .setActions(
//                PlaybackStateCompat.ACTION_PLAY or
//                        PlaybackStateCompat.ACTION_PAUSE or
//                        PlaybackStateCompat.ACTION_PLAY_PAUSE or
//                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
//            )
//        mediaSession.setPlaybackState(mediaStateBuilder.build())
//        mediaSession.setCallback(SessionCallback)
//        mediaSession.isActive = true
//    }
}