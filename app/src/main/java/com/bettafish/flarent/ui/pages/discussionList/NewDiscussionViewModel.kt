package com.bettafish.flarent.ui.pages.discussionList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.data.DiscussionsRepository
import com.bettafish.flarent.data.TagsRepository
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewDiscussionViewModel(
    private val discussionsRepository: DiscussionsRepository,
    private val tagsRepository: TagsRepository
) : ViewModel() {

    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting

    private val _createdDiscussion = MutableStateFlow<Discussion?>(null)
    val createdDiscussion: StateFlow<Discussion?> = _createdDiscussion

    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags: StateFlow<List<Tag>> = _tags.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadTags()
    }

    private fun loadTags() {
        viewModelScope.launch {
            try {
                _tags.value = tagsRepository.fetchTags()
            } catch (_: Exception) {}
        }
    }

    fun post(title: String, content: String, tag: Tag) {
        viewModelScope.launch {
            try {
                _isPosting.value = true
                _error.value = null
                val result = discussionsRepository.createDiscussion(title, content, tag.id)
                _createdDiscussion.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "发送失败"
            } finally {
                _isPosting.value = false
            }
        }
    }
}
