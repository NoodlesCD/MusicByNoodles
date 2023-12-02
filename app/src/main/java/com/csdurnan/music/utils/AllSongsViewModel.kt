package com.csdurnan.music.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.csdurnan.music.dc.Song

class AllSongsViewModel : ViewModel() {
    private val _data = MutableLiveData<Song>()
    val data: LiveData<Song>
        get() = _data
}