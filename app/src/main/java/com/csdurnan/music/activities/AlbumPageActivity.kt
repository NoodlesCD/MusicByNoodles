package com.csdurnan.music.activities

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.csdurnan.music.R

class AlbumPageActivity : AppCompatActivity() {
    /**
     * savedInstanceState is a bundle that contains the state of an activity.
     * Saved in response to an event such as rotating the device or activity being paused.
     * Can restore the state of an activity when it is recreated.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.album_page)
    }
}