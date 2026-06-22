package com.bettafish.flarent.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
@Type("polls")
class Poll {
    @Id
    lateinit var id: String

    @JsonProperty("question")
    var question: String? = null

    @JsonProperty("canVote")
    var canVote: Boolean? = null

    @JsonProperty("hasVoted")
    var hasVoted: Boolean? = null

    @JsonProperty("publicPoll")
    var publicPoll: Boolean? = null

    @JsonProperty("allowMultipleVotes")
    var allowMultipleVotes: Boolean? = null

    @JsonProperty("maxVotes")
    var maxVotes: Int? = null

    @JsonProperty("voteCount")
    var voteCount: Int? = null

    @JsonProperty("allowPollOptionImage")
    var allowPollOptionImage: Boolean? = null

    @JsonProperty("endDate")
    @Contextual
    var endDate: ZonedDateTime? = null

    @JsonProperty("imageUrl")
    var imageUrl: String? = null

    @Relationship("options")
    var options: List<PollOption>? = null

    @Relationship("myVotes")
    var myVotes: List<PollOption>? = null
}

@Serializable
@Type("poll_options")
class PollOption {
    @Id
    lateinit var id: String

    @JsonProperty("answer")
    var answer: String? = null

    @JsonProperty("imageUrl")
    var imageUrl: String? = null

    @JsonProperty("voteCount")
    var voteCount: Int? = null

    @JsonProperty("isVoted")
    var isVoted: Boolean? = null

    @Relationship("poll")
    var poll: Poll? = null
}
