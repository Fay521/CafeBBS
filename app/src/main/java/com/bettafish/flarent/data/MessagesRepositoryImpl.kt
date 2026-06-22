package com.bettafish.flarent.data

import com.bettafish.flarent.models.Message
import com.bettafish.flarent.models.MessageDialog
import com.bettafish.flarent.network.FlarumService

class MessagesRepositoryImpl(
    private val service: FlarumService
) : MessagesRepository {

    override suspend fun fetchDialogs(options: Map<String, String>): List<MessageDialog> {
        return service.getDialogs(options)
    }

    override suspend fun fetchDialog(id: String, include: String?): MessageDialog {
        return service.getDialog(id, include)
    }

    override suspend fun fetchMessages(
        dialogId: String,
        options: Map<String, String>
    ): List<Message> {
        return service.getMessages(dialogId, options)
    }

    override suspend fun sendMessage(dialogId: String, message: String): Message {
        val msg = Message().apply {
            val d = MessageDialog().apply { id = dialogId }
            this.dialog = d
            this.message = message
        }
        return service.sendMessage(msg)
    }
}
