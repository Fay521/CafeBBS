package com.bettafish.flarent.ui.pages.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bettafish.flarent.data.MessagesRepository
import com.bettafish.flarent.models.MessageDialog
import kotlinx.coroutines.flow.Flow

class MessagesViewModel(
    private val repository: MessagesRepository
) : ViewModel() {

    companion object {
        const val PAGE_SIZE = 20
    }

    val dialogs: Flow<PagingData<MessageDialog>> = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            MessagesDataSource(repository)
        }
    ).flow.cachedIn(viewModelScope)
}
