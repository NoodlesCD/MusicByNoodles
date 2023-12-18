package com.csdurnan.music.ui.songs.currentSong

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SongSelectorViewModel : ViewModel() {
    private val mutableSelectedItem = MutableLiveData<Int>()
    val selectedItem: LiveData<Int> get() = mutableSelectedItem

    fun selectSong(songIndex: Int) {
        mutableSelectedItem.value = songIndex
    }
}