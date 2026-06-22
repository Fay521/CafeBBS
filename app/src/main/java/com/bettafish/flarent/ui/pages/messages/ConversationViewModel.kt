package com.bettafish.flarent.ui.pages.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.data.MessagesRepository
import com.bettafish.flarent.models.Message
import com.bettafish.flarent.models.MessageDialog
import com.bettafish.flarent.utils.SuspendCommand1
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConversationViewModel(
    private val repository: MessagesRepository
) : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _dialog = MutableStateFlow<MessageDialog?>(null)
    val dialog: StateFlow<MessageDialog?> = _dialog

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val sendCommand = SuspendCommand1<String>({ text ->
        sendMessage(text)
    }, viewModelScope)

    fun loadDialog(dialogId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val dialog = repository.fetchDialog(dialogId)
                _dialog.value = dialog
                _messages.value = dialog.lastMessage?.let { msg ->
                    // Messages come in the dialog's included data
                    listOf(msg)
                } ?: emptyList()
            } catch (_: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh(dialogId: String) {
        viewModelScope.launch {
            try {
                val options = mapOf(
                    "page[limit]" to "50",
                    "include" to "user"
                )
                val fetchedMessages = repository.fetchMessages(dialogId, options)
                _messages.value = fetchedMessages
            } catch (_: Exception) {
            }
        }
    }

    private suspend fun sendMessage(text: String) {
        try {
            val dialogId = _dialog.value?.id ?: return
            val newMessage = repository.sendMessage(dialogId, text)
            _messages.value = _messages.value + newMessage
        } catch (_: Exception) {
        }
    }
}
