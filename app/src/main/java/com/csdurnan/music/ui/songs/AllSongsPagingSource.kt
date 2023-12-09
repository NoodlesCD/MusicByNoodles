package com.csdurnan.music.ui.songs

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.csdurnan.music.dc.Song

class AllSongsPagingSource(private val songsList: List<Song>) : PagingSource<Int, Song>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
        return try {
            val currentPage = params.key ?: 0
            val dataChunk = songsList.subList(currentPage * params.loadSize, (currentPage + 1) * params.loadSize)

            LoadResult.Page(
                data = dataChunk,
                prevKey = if (currentPage == 0) null else currentPage -1,
                nextKey = if ((currentPage + 1) * params.loadSize >= songsList.size) null else currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Song>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}