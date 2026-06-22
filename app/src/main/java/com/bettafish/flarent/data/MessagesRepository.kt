package com.bettafish.flarent.data

import com.bettafish.flarent.models.Message
import com.bettafish.flarent.models.MessageDialog

interface MessagesRepository {
    suspend fun fetchDialogs(options: Map<String, String>): List<MessageDialog>
    suspend fun fetchDialog(id: String, include: String? = "messages,messages.user"): MessageDialog
    suspend fun fetchMessages(dialogId: String, options: Map<String, String>): List<Message>
    suspend fun sendMessage(dialogId: String, message: String): Message
}
