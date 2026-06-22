package com.bettafish.flarent.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
@Type("dialogs")
class MessageDialog {
    @Id
    lateinit var id: String

    @JsonProperty("title")
    var title: String? = null

    @JsonProperty("lastMessageAt")
    @Contextual
    var lastMessageAt: ZonedDateTime? = null

    @JsonProperty("unreadCount")
    var unreadCount: Int? = null

    @JsonProperty("messageCount")
    var messageCount: Int? = null

    @Relationship("lastMessage")
    var lastMessage: Message? = null

    @Relationship("recipients")
    var recipients: List<User>? = null
}

@Serializable
@Type("messages")
class Message : Section {
    @Id
    lateinit var id: String

    @JsonProperty("message")
    var message: String? = null

    @JsonProperty("contentHtml")
    var contentHtml: String? = null

    @JsonProperty("isRead")
    var isRead: Boolean? = null

    @JsonProperty("createdAt")
    @Contextual
    var createdAt: ZonedDateTime? = null

    @Relationship("user")
    var user: User? = null

    @Relationship("dialog")
    var dialog: MessageDialog? = null
}
