package com.bettafish.flarent.ui.widgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.data.PollsRepository
import com.bettafish.flarent.models.Poll
import com.bettafish.flarent.utils.SuspendCommand1
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PollViewModel(
    private val pollId: String,
    private val initialPoll: Poll? = null,
    private val repository: PollsRepository
) : ViewModel() {
    private val _poll = MutableStateFlow<Poll?>(initialPoll)
    val poll: StateFlow<Poll?> = _poll

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val voteCommand = SuspendCommand1<String>({ optionId ->
        votePoll(optionId)
    }, viewModelScope)

    init {
        if (initialPoll == null || initialPoll.options.isNullOrEmpty()) {
            viewModelScope.launch {
                try {
                    _isLoading.value = true
                    _poll.value = repository.fetchPoll(pollId)
                } catch (_: Exception) {
                    // Keep initial poll data on error
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    private suspend fun votePoll(optionId: String) {
        try {
            _isLoading.value = true
            val updatedPoll = repository.votePoll(pollId, listOf(optionId))
            _poll.value = updatedPoll
        } catch (_: Exception) {
            // Silently handle vote errors
        } finally {
            _isLoading.value = false
        }
    }

    fun updatePoll(poll: Poll) {
        _poll.value = poll
    }
}
