package com.bettafish.flarent.data

import com.bettafish.flarent.models.Poll
import com.bettafish.flarent.network.FlarumService

class PollsRepositoryImpl(
    private val service: FlarumService
) : PollsRepository {

    override suspend fun fetchPoll(id: String, include: String?): Poll {
        return service.getPoll(id, include)
    }

    override suspend fun votePoll(pollId: String, optionIds: List<String>): Poll {
        val body = mapOf<String, Any?>(
            "data" to mapOf(
                "type" to "polls",
                "id" to pollId,
                "attributes" to mapOf(
                    "optionIds" to optionIds
                )
            )
        )
        return service.votePoll(pollId, body)
    }

    override suspend fun createPoll(
        discussionId: String,
        question: String,
        options: List<String>,
        publicPoll: Boolean,
        allowMultipleVotes: Boolean,
        maxVotes: Int?,
        endDate: String?,
        allowOptionImage: Boolean
    ): Poll {
        val attrs = mutableMapOf<String, Any?>(
            "question" to question,
            "publicPoll" to publicPoll,
            "allowMultipleVotes" to allowMultipleVotes,
            "allowPollOptionImage" to allowOptionImage
        )
        maxVotes?.let { attrs["maxVotes"] = it }
        endDate?.let { attrs["endDate"] = it }

        val body = mapOf(
            "data" to mapOf(
                "type" to "polls",
                "attributes" to attrs,
                "relationships" to mapOf(
                    "discussion" to mapOf(
                        "data" to mapOf(
                            "type" to "discussions",
                            "id" to discussionId
                        )
                    ),
                    "options" to mapOf(
                        "data" to options.mapIndexed { index, answer ->
                            mapOf(
                                "type" to "poll_options",
                                "attributes" to mapOf("answer" to answer)
                            )
                        }
                    )
                )
            )
        )
        return service.createPoll(body)
    }
}
