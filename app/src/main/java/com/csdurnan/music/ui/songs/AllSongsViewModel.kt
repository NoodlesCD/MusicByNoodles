package com.csdurnan.music.ui.songs

import android.content.ContentResolver
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.csdurnan.music.ContentManagement
import com.csdurnan.music.dc.Song
import kotlinx.coroutines.flow.Flow

class AllSongsViewModel : ViewModel() {
    @RequiresApi(Build.VERSION_CODES.Q)
    private lateinit var contentManagement: ContentManagement

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getAllSongsPagingData(): Flow<PagingData<Song>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { contentManagement.allSongsPagingSource() }
        ).flow
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setContentManagement(contentResolver: ContentResolver) {
        contentManagement = ContentManagement(contentResolver)
    }
}