package com.bettafish.flarent.ui.pages.messages

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bettafish.flarent.data.MessagesRepository
import com.bettafish.flarent.models.MessageDialog

class MessagesDataSource(
    private val repository: MessagesRepository
) : PagingSource<Int, MessageDialog>() {

    override fun getRefreshKey(state: PagingState<Int, MessageDialog>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageDialog> {
        return try {
            val page = params.key ?: 0
            val options = mutableMapOf(
                "page[offset]" to (page * MessagesViewModel.PAGE_SIZE).toString(),
                "page[limit]" to MessagesViewModel.PAGE_SIZE.toString(),
                "include" to "lastMessage,recipients"
            )
            val dialogs = repository.fetchDialogs(options)
            val nextKey = if (dialogs.size < MessagesViewModel.PAGE_SIZE) null else page + 1
            val prevKey = if (page > 0) page - 1 else null

            LoadResult.Page(
                data = dialogs,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
